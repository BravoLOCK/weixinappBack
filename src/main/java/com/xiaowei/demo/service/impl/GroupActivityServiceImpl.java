package com.xiaowei.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowei.demo.entity.Cart;
import com.xiaowei.demo.entity.GroupActivity;
import com.xiaowei.demo.entity.Product;
import com.xiaowei.demo.entity.User;
import com.xiaowei.demo.entity.GroupMember;
import com.xiaowei.demo.mapper.*;
import com.xiaowei.demo.service.GroupActivityService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class GroupActivityServiceImpl extends ServiceImpl<GroupActivityMapper, GroupActivity> implements GroupActivityService {

    @Autowired
    private GroupActivityMapper groupActivityMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private GroupMemberMapper groupMemberMapper;

    @Override
    @Transactional(rollbackFor = Exception.class) // 回滚所有异常，包括检查型异常
    public boolean createGroupActivity(GroupActivity activity) {
        // 验证商品是否存在且可团购
        Product product = productMapper.selectById(activity.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (product.getStatus() != 1) {
            throw new RuntimeException("商品已下架");
        }
        if (product.getStock() <= 0) {
            throw new RuntimeException("商品库存不足");
        }

        // 设置默认值
        activity.setCurrentMembers(0);
        activity.setStatus(0); // 未开始

        // 使用商品团购价（如果请求中未指定）
        if (activity.getGroupPrice() == null) {
            activity.setGroupPrice(product.getGroupPrice());
        }

        // 如果是立即开始的活动
        if (activity.getStartTime().isBefore(LocalDateTime.now()) ||
                activity.getStartTime().isEqual(LocalDateTime.now())) {
            activity.setStatus(1); // 进行中
        }

        // 保存活动
        groupActivityMapper.insert(activity);

        //加入购物车
        Cart cart = new Cart();
        cart.setActivityId(activity.getId());
        cart.setProductId(activity.getProductId());
        cart.setUserId(activity.getLeaderId());
        cart.setQuantity(activity.getQuantity());
        cartMapper.insert(cart);


        //将发起人插入团购成员表
        GroupMember member = new GroupMember();
        member.setGroupActivityId(activity.getId());
        member.setUserId(activity.getLeaderId());
        member.setIsLeader(1);
        groupMemberMapper.insert(member);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class) // 回滚所有异常，包括检查型异常
    public boolean joinGroupActivity(String activityId, String userId, Integer quantity) {

        // 检查活动是否存在且可参加
        GroupActivity activity = groupActivityMapper.selectById(activityId);
        if (activity == null) {
            throw new RuntimeException("团购活动不存在");
        }

        if (activity.getStatus() != 1) {
            throw new RuntimeException("团购活动不在进行中");
        }

        if (activity.getCurrentMembers() >= activity.getTargetMembers()) {
            throw new RuntimeException("团购活动已满员");
        }

        // 检查是否已参加
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupMember::getGroupActivityId, activityId)
                .eq(GroupMember::getUserId, userId);
        Long count = groupMemberMapper.selectCount(wrapper);
        if (count > 0) {
            throw new RuntimeException("您已参加该团购活动");
        }

        // 创建团购成员记录
        GroupMember member = new GroupMember();
        member.setGroupActivityId(activityId);
        member.setUserId(userId);
        member.setIsLeader(0);

        boolean success = groupMemberMapper.insert(member) > 0;

        if (success) {
            // 更新团购人数
            activity.setCurrentMembers(activity.getCurrentMembers() + 1);
            groupActivityMapper.updateById(activity);

            // 检查是否已成团
            if (activity.getCurrentMembers() >= activity.getTargetMembers()) {
                activity.setStatus(2); // 已成团
                groupActivityMapper.updateById(activity);
            }

            //加入购物车
            Cart cart = new Cart();
            cart.setActivityId(activity.getId());
            cart.setProductId(activity.getProductId());
            cart.setUserId(userId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }

        return success;
    }
}
