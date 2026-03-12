package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.Product;
import com.xiaowei.demo.service.ProductService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "商品管理")
@RestController
@RequestMapping("/products")
@Validated
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 新增商品
     */
    @ApiOperation("新增商品")
    @PostMapping("/add")
    public Result<?> addProduct(@Valid @RequestBody Product product) {

        log.debug(product.toString());

        // 检查商品名是否已存在
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getName, product.getName());
        if (productService.count(wrapper) > 0) {
            return Result.error("商品名已存在");
        }

        boolean success = productService.save(product);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteProduct(@PathVariable String id) {
        boolean success = productService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新商品
     */
    @PutMapping("/update")
    public Result<?> updateProduct(@RequestBody Product product) {
        boolean success = productService.updateById(product);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID查询商品
     */
    @GetMapping("/get/{id}")
    public Result<Product> getProductById(@PathVariable String id) {
        Product product = productService.getById(id);
        product.setDiscount();
        return product != null ? Result.success(product) : Result.error("商品不存在");
    }

    /**
     * 分页查询商品列表
     */
    @GetMapping("/list")
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String orderSign,
            @RequestParam(required = false) String keyword) {

        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();

        if (orderSign != null && !orderSign.trim().isEmpty()) {
            if(orderSign.equalsIgnoreCase("hot"))
                wrapper.orderByDesc(Product::getSales);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Product::getName, keyword)
                    .or()
                    .like(Product::getDescription, keyword);
        }

        if (categoryId != null && !categoryId.trim().isEmpty()) {
            wrapper.like(Product::getCategoryId, categoryId);
        }

        Page<Product> result = productService.page(page, wrapper);

        //计算折扣
        for (Product entity : result.getRecords()) {
            entity.setDiscount();
        }

        return Result.success(result);
    }
}
