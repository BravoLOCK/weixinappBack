package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.User;
import com.xiaowei.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 新增用户
     */
    @PostMapping("/add")
    public Result<?> addUser(@RequestBody User user) {

        log.debug(user.toString());

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, user.getUsername());
        if (userService.count(wrapper) > 0) {
            return Result.error("用户名已存在");
        }

        boolean success = userService.save(user);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteUser(@PathVariable String id) {
        boolean success = userService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新用户
     */
    @PutMapping("/update")
    public Result<?> updateUser(@RequestBody User user) {
        boolean success = userService.updateById(user);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/get/{id}")
    public Result<User> getUserById(@PathVariable String id) {
        User user = userService.getById(id);
        return user != null ? Result.success(user) : Result.error("用户不存在");
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/list")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<User> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                    .or()
                    .like(User::getEmail, keyword)
                    .or()
                    .like(User::getPhone, keyword);
        }

        Page<User> result = userService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 条件查询用户
     */
    @GetMapping("/query")
    public Result<List<User>> queryUsers(@RequestParam(required = false) String keyword) {
        List<User> users = userService.queryUsers(keyword);
        return Result.success(users);
    }
}