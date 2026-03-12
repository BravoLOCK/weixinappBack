package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.Order;
import com.xiaowei.demo.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "*", maxAge = 3600)
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 新增订单
     */
    @PostMapping("/add")
    public Result<?> addOrder(@RequestBody Order order) {

        log.debug(order.toString());

        boolean success = orderService.save(order);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteOrder(@PathVariable String id) {
        boolean success = orderService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新订单
     */
    @PutMapping("/update")
    public Result<?> updateOrder(@RequestBody Order order) {
        boolean success = orderService.updateById(order);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID查询订单
     */
    @GetMapping("/get/{id}")
    public Result<Order> getOrderById(@PathVariable String id) {
        Order order = orderService.getById(id);
        return order != null ? Result.success(order) : Result.error("用户不存在");
    }

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    public Result<Page<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        //TUDO：此处需要关联商品表进行更复杂的查询 by xiaowei.zu
        /**
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Order::getReceiverName, keyword)
                    .or()
                    .like(Order::getReceiverPhone, keyword)
                    .or()
                    .like(Order::getDetailAddress, keyword);
        }
         */

        Page<Order> result = orderService.page(page, wrapper);
        return Result.success(result);
    }

}