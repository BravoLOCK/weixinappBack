package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.UserAddress;
import com.xiaowei.demo.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user-address")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 新增用户地址
     */
    @PostMapping("/add")
    public Result<?> addUserAddress(@RequestBody UserAddress userAddress) {

        log.debug(userAddress.toString());

        boolean success = userAddressService.save(userAddress);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除用户地址
     */
    @PostMapping("/delete/{id}")
    public Result<?> deleteUserAddress(@PathVariable String id) {
        boolean success = userAddressService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新用户地址
     */
    @PostMapping("/update")
    public Result<?> updateUserAddress(@RequestBody UserAddress userAddress) {
        boolean success = userAddressService.updateById(userAddress);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID查询用户地址
     */
    @GetMapping("/get/{id}")
    public Result<UserAddress> getUserAddressById(@PathVariable String id) {
        UserAddress userAddress = userAddressService.getById(id);
        return userAddress != null ? Result.success(userAddress) : Result.error("用户不存在");
    }

    /**
     * 分页查询用户地址列表
     */
    @GetMapping("/list")
    public Result<Page<UserAddress>> getUserAddressList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<UserAddress> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(UserAddress::getReceiverName, keyword)
                    .or()
                    .like(UserAddress::getReceiverPhone, keyword)
                    .or()
                    .like(UserAddress::getDetailAddress, keyword);
        }

        Page<UserAddress> result = userAddressService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 查询指定用户的所有收货地址
     */
    @GetMapping("/by-user/{userId}")
    public Result<List<UserAddress>> getAddressByUserId(@PathVariable String userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
               .orderByDesc(UserAddress::getIsDefault)
               .orderByDesc(UserAddress::getCreateTime);
        List<UserAddress> list = userAddressService.list(wrapper);
        return Result.success(list);
    }

}