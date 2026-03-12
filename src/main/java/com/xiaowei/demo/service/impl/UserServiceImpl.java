package com.xiaowei.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowei.demo.entity.User;
import com.xiaowei.demo.mapper.UserMapper;
import com.xiaowei.demo.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public List<User> queryUsers(String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(keyword)) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getEmail, keyword)
                    .or()
                    .like(User::getPhone, keyword);
        }

        return this.list(wrapper);
    }

    @Override
    public User login(User params) {

        User user = baseMapper.selectByUsername(params.getUsername(), params.getPassword());
        if (user == null) {
            System.out.println("数据库不存在满足条件的数据。");
            return null;
        }

        return user;
    }
}
