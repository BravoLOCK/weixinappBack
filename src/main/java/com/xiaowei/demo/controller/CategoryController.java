package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.Category;
import com.xiaowei.demo.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增类别
     */
    @PostMapping("/add")
    public Result<?> addCategory(@RequestBody Category category) {

        log.debug(category.toString());

        // 检查类别名是否已存在
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getName, category.getName());
        if (categoryService.count(wrapper) > 0) {
            return Result.error("该类别已经存在");
        }

        boolean success = categoryService.save(category);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除类别
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteCategory(@PathVariable String id) {
        boolean success = categoryService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新类别
     */
    @PutMapping("/update")
    public Result<?> updateCategory(@RequestBody Category category) {
        boolean success = categoryService.updateById(category);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID查询类别
     */
    @GetMapping("/get/{id}")
    public Result<Category> getCategoryById(@PathVariable String id) {
        Category category = categoryService.getById(id);
        return category != null ? Result.success(category) : Result.error("类别不存在");
    }

    /**
     * 分页查询类别列表
     */
    @GetMapping("/list")
    public Result<Page<Category>> getCategoryList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<Category> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Category::getName, keyword);
        }

        Page<Category> result = categoryService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 条件查询类别
     */
    @GetMapping("/all")
    public Result<List<Category>> getAllCategorys() {
        List<Category> categorys = categoryService.list();
        return Result.success(categorys);
    }
}