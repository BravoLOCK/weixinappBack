package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.common.constant.GroupActivityStatus;
import com.xiaowei.demo.entity.GroupActivity;
import com.xiaowei.demo.entity.GroupMember;
import com.xiaowei.demo.entity.Product;
import com.xiaowei.demo.service.GroupActivityService;
import com.xiaowei.demo.service.GroupMemberService;
import com.xiaowei.demo.service.ProductService;
import com.xiaowei.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/group-activity")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GroupActivityController {

    @Autowired
    private GroupActivityService groupActivityService;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    /**
     * 新增团购活动
     */
    @PostMapping("/add")
    public Result<?> addGroupActivity(@RequestBody GroupActivity groupActivity) {

        log.debug(groupActivity.toString());

        boolean success = groupActivityService.save(groupActivity);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除团购活动
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteGroupActivity(@PathVariable String id) {
        boolean success = groupActivityService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新团购活动
     */
    @PutMapping("/update")
    public Result<?> updateGroupActivity(@RequestBody GroupActivity groupActivity) {
        boolean success = groupActivityService.updateById(groupActivity);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID查询团购活动
     */
    @GetMapping("/get/{id}")
    public Result<GroupActivity> getGroupActivityById(@PathVariable String id) {
        GroupActivity groupActivity = groupActivityService.getById(id);
        return groupActivity != null ? Result.success(groupActivity) : Result.error("用户不存在");
    }

    /**
     * 分页查询团购活动列表
     */
    @GetMapping("/list")
    public Result<Page<GroupActivity>> getGroupActivityList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String productId,
            @RequestParam(required = false) String leaderId,
            @RequestParam(required = false) String keyword) {

        Page<GroupActivity> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GroupActivity> wrapper = new LambdaQueryWrapper<>();

        if (productId != null && !productId.trim().isEmpty()) {
            wrapper.eq(GroupActivity::getProductId, productId);
        }

        if (leaderId != null && !leaderId.trim().isEmpty()) {
            wrapper.eq(GroupActivity::getLeaderId, leaderId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(GroupActivity::getTitle, keyword);
        }

        Page<GroupActivity> result = groupActivityService.page(page, wrapper);

        //为扩展字段赋值
        for (GroupActivity entity : result.getRecords()) {
            //获取参与团购的成员，并填充成员的用户信息
            LambdaQueryWrapper<GroupMember> wrapper_member = new LambdaQueryWrapper<>();
            wrapper_member.eq(GroupMember::getGroupActivityId, entity.getId());
            List<GroupMember> members = groupMemberService.list(wrapper_member);
            for (GroupMember member : members) {
                member.setUser(userService.getById(member.getUserId()));
            }
            entity.setMembers(members);
            //获取团长对象
            entity.setLeader(userService.getById(entity.getLeaderId()));
            //获取商品对象
            entity.setProduct(productService.getById(entity.getProductId()));
            //设置剩余时间
            entity.setRemainingHours();
            //设置团购进度
            entity.setProgress(entity.getTargetMembers() != null && entity.getTargetMembers() > 0
                    ? (double) entity.getCurrentMembers() / entity.getTargetMembers() * 100 : 0.0);
            //设置是否可参与
            entity.setCanJoin(entity.canJoin());
            //团购价回填：活动无独立价格时取商品团购价
            if (entity.getGroupPrice() == null && entity.getProduct() != null) {
                entity.setGroupPrice(entity.getProduct().getGroupPrice());
            }
        }

        return Result.success(result);
    }

    /**
     * 根据商品ID查询可参与的团购活动（商品详情页使用）
     */
    @GetMapping("/by-product/{productId}")
    public Result<List<GroupActivity>> getActivitiesByProductId(@PathVariable String productId) {
        LambdaQueryWrapper<GroupActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(GroupActivity::getProductId, productId)
               .in(GroupActivity::getStatus, GroupActivityStatus.NOT_STARTED, GroupActivityStatus.IN_PROGRESS)
               .orderByAsc(GroupActivity::getEndTime);

        List<GroupActivity> list = groupActivityService.list(wrapper);

        for (GroupActivity entity : list) {
            // 获取参与团购的成员，并填充成员的用户信息
            LambdaQueryWrapper<GroupMember> memberWrapper = new LambdaQueryWrapper<>();
            memberWrapper.eq(GroupMember::getGroupActivityId, entity.getId());
            List<GroupMember> members = groupMemberService.list(memberWrapper);
            for (GroupMember member : members) {
                member.setUser(userService.getById(member.getUserId()));
            }
            entity.setMembers(members);
            // 获取团长信息
            entity.setLeader(userService.getById(entity.getLeaderId()));
            // 获取商品信息
            entity.setProduct(productService.getById(entity.getProductId()));
            // 设置剩余时间
            entity.setRemainingHours();
            // 设置团购进度
            entity.setProgress(entity.getTargetMembers() != null && entity.getTargetMembers() > 0
                    ? (double) entity.getCurrentMembers() / entity.getTargetMembers() * 100 : 0.0);
            // 设置是否可参与
            entity.setCanJoin(entity.canJoin());
            // 团购价回填：活动无独立价格时取商品团购价
            if (entity.getGroupPrice() == null && entity.getProduct() != null) {
                entity.setGroupPrice(entity.getProduct().getGroupPrice());
            }
        }

        return Result.success(list);
    }

    /**
     * 创建团购活动
     */
    @PostMapping("/create")
    public Result<?> createGroupActivity(@RequestBody GroupActivity groupActivity) {
        boolean success = groupActivityService.createGroupActivity(groupActivity);
        return success ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 加入团购活动
     */
    @PostMapping("/join")
    public Result<?> joinGroupActivity(@RequestParam String activityId, @RequestParam String userId, @RequestParam Integer quantity) {
        boolean success = groupActivityService.joinGroupActivity(activityId, userId, quantity);
        return success ? Result.success("加入成功") : Result.error("加入失败");
    }

    /**
     * 查询用户参与的所有团购活动（含所有状态）
     */
    @GetMapping("/by-member/{userId}")
    public Result<List<GroupActivity>> getActivitiesByMemberUserId(@PathVariable String userId) {
        // 先查出该用户的所有成员记录
        LambdaQueryWrapper<GroupMember> memberWrapper = new LambdaQueryWrapper<>();
        memberWrapper.eq(GroupMember::getUserId, userId);
        List<GroupMember> memberRecords = groupMemberService.list(memberWrapper);

        if (memberRecords.isEmpty()) {
            return Result.success(java.util.Collections.emptyList());
        }

        List<String> activityIds = memberRecords.stream()
                .map(GroupMember::getGroupActivityId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());

        LambdaQueryWrapper<GroupActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(GroupActivity::getId, activityIds)
               .orderByDesc(GroupActivity::getCreateTime);

        List<GroupActivity> list = groupActivityService.list(wrapper);

        for (GroupActivity entity : list) {
            LambdaQueryWrapper<GroupMember> mw = new LambdaQueryWrapper<>();
            mw.eq(GroupMember::getGroupActivityId, entity.getId());
            List<GroupMember> members = groupMemberService.list(mw);
            for (GroupMember member : members) {
                member.setUser(userService.getById(member.getUserId()));
            }
            entity.setMembers(members);
            entity.setLeader(userService.getById(entity.getLeaderId()));
            entity.setProduct(productService.getById(entity.getProductId()));
            entity.setRemainingHours();
            entity.setProgress(entity.getTargetMembers() != null && entity.getTargetMembers() > 0
                    ? (double) entity.getCurrentMembers() / entity.getTargetMembers() * 100 : 0.0);
            entity.setCanJoin(entity.canJoin());
            if (entity.getGroupPrice() == null && entity.getProduct() != null) {
                entity.setGroupPrice(entity.getProduct().getGroupPrice());
            }
        }

        return Result.success(list);
    }
}