package com.xiaowei.demo.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xiaowei.demo.common.Result;
import com.xiaowei.demo.entity.GroupMember;
import com.xiaowei.demo.entity.GroupMember;
import com.xiaowei.demo.service.GroupMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/group-member")
@CrossOrigin(origins = "*", maxAge = 3600)
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    /**
     * 新增团购活动
     */
    @PostMapping("/add")
    public Result<?> addGroupMember(@RequestBody GroupMember groupMember) {

        log.debug(groupMember.toString());

        boolean success = groupMemberService.save(groupMember);
        return success ? Result.success("添加成功") : Result.error("添加失败");
    }

    /**
     * 删除团购活动
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteGroupMember(@PathVariable String id) {
        boolean success = groupMemberService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 更新团购活动
     */
    @PutMapping("/update")
    public Result<?> updateGroupMember(@RequestBody GroupMember groupMember) {
        boolean success = groupMemberService.updateById(groupMember);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 根据ID查询团购活动
     */
    @GetMapping("/get/{id}")
    public Result<GroupMember> getGroupMemberById(@PathVariable String id) {
        GroupMember groupMember = groupMemberService.getById(id);
        return groupMember != null ? Result.success(groupMember) : Result.error("用户不存在");
    }

    /**
     * 分页查询团购活动列表
     */
    @GetMapping("/list")
    public Result<Page<GroupMember>> getGroupMemberList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {

        Page<GroupMember> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<GroupMember> wrapper = new LambdaQueryWrapper<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            //TODU:这里需要关联查询
        }

        Page<GroupMember> result = groupMemberService.page(page, wrapper);
        return Result.success(result);
    }

}