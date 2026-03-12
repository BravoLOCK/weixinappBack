package com.xiaowei.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends BaseEntity {

    private String name;

    @TableField("parent_id")
    private String parentId = "0";

    private String icon;

    @TableField("sort_order")
    private Integer sortOrder = 0;

    private Integer status = 1; // 状态：0-禁用，1-启用
}
