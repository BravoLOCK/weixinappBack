package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xiaowei.demo.common.BusinessException;
import com.xiaowei.demo.common.LogRecord;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.User;
import com.xiaowei.demo.service.UserService;
import com.xiaowei.demo.utils.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    @LogRecord
    public Result<?> login(@RequestBody User params) {
        User user = userService.login(params);
        log.debug("调用login方法获取登录对象："+params.toString());

        if (user == null) {
            //return Result.error(401, "用户名或密码错误");
            throw new BusinessException("401", "用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            return Result.error(403, "账号已被禁用");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        log.debug("生成令牌参数："+user.getId()+"&"+user.getUsername());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", user);

        log.debug("生成的令牌："+token);
        return Result.success(data);
    }

    @PostMapping("/register")
    @LogRecord
    public Result<String> register(@RequestBody User user) {

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        if(userService.save(user)){
            return Result.success("注册成功");
        }

        return Result.error("注册失败，用户名已存在");


    }

}
