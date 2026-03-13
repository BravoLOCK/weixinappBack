package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.Cart;
import com.xiaowei.demo.entity.GroupActivity;
import com.xiaowei.demo.service.CartService;
import com.xiaowei.demo.service.GroupActivityService;
import com.xiaowei.demo.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private GroupActivityService groupActivityService;

    /**
     * 新增购物车
     */
    @PostMapping("/add")
    public Result<?> addCart(@RequestBody Cart cart) {

        log.debug(cart.toString());

        boolean success = cartService.save(cart);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除购物车
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCart(@PathVariable String id) {
        boolean success = cartService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新购物车
     */
    @PutMapping("/update")
    public Result<?> updateCart(@RequestBody Cart cart) {
        boolean success = cartService.updateById(cart);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID查询购物车
     */
    @GetMapping("/get/{id}")
    public Result<Cart> getCartById(@PathVariable String id) {
        Cart cart = cartService.getById(id);
        return cart != null ? Result.success(cart) : Result.error("用户不存在");
    }

    /**
     * 分页查询购物车列表
     */
    @GetMapping("/list")
    public Result<Page<Cart>> getCartList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String keyword) {

        Page<Cart> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();

        if (userId != null && !userId.trim().isEmpty()) {
            wrapper.eq(Cart::getUserId, userId);
        }

        //TUDO：此处需要关联商品表进行更复杂的查询 by xiaowei.zu
        /**
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Cart::getReceiverName, keyword)
                    .or()
                    .like(Cart::getReceiverPhone, keyword)
                    .or()
                    .like(Cart::getDetailAddress, keyword);
        }
         */

        Page<Cart> result = cartService.page(page, wrapper);
        //为扩展字段赋值
        for (Cart entity : result.getRecords()) {
            entity.setProduct(productService.getById(entity.getProductId()));
            entity.setActivity(groupActivityService.getById(entity.getActivityId()));
        }

        return Result.success(result);
    }

}