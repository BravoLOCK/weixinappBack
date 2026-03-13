package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.Order;
import com.xiaowei.demo.service.OrderService;
import com.xiaowei.demo.service.ProductService;
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

    @Autowired
    private ProductService productService;

    /**
     * 新增订单
     */
    @PostMapping("/add")
    public Result<Order> addOrder(@RequestBody Order order) {

        log.debug(order.toString());

        // 未传 orderNo 时服务端自动生成
        if (order.getOrderNo() == null || order.getOrderNo().trim().isEmpty()) {
            order.setOrderNo("ORD" + System.currentTimeMillis());
        }

        boolean success = orderService.save(order);
        if (!success) {
            return Result.error("添加失败");
        }

        // 回填商品信息，方便前端直接使用
        if (order.getProductId() != null) {
            order.setProduct(productService.getById(order.getProductId()));
        }

        return Result.success(order);
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
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (order.getProductId() != null) {
            order.setProduct(productService.getById(order.getProductId()));
        }
        return Result.success(order);
    }

    /**
     * 分页查询订单列表
     */
    @GetMapping("/list")
    public Result<Page<Order>> getOrderList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String keyword) {

        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();

        if (userId != null && !userId.trim().isEmpty()) {
            wrapper.eq(Order::getUserId, userId);
        }

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

    /**
     * 取消订单（将状态置为已取消）
     */
    @PostMapping("/cancel/{id}")
    public Result<?> cancelOrder(@PathVariable String id) {
        Order order = orderService.getById(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.canCancel()) {
            return Result.error("当前订单状态不允许取消");
        }
        order.setStatus(4);
        boolean success = orderService.updateById(order);
        return success ? Result.success("取消成功") : Result.error("取消失败");
    }

}