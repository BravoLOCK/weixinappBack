package com.xiaowei.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("product")
public class Product extends BaseEntity {

    private String name;
    private String description;

    @TableField("category_id")
    private String categoryId;

    @TableField("main_image")
    private String mainImage;

    @TableField("images")
    private String images; // JSON格式存储

    private BigDecimal originalPrice;
    private BigDecimal groupPrice;
    private Integer stock;
    private Integer sales;

    @TableField("min_group_members")
    private Integer minGroupMembers;

    @TableField("max_group_members")
    private Integer maxGroupMembers;

    @TableField("group_duration")
    private Integer groupDuration;

    private Integer status;

    @TableField("sort_order")
    private Integer sortOrder;

    @TableField("merchant_id")
    private String merchantId;

    @TableField("purchase_limit")
    private Integer purchaseLimit;

    @TableField(exist = false)
    private BigDecimal discount;

    public void setDiscount() {
        this.discount = this.groupPrice.divide(this.originalPrice, 2, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(10));
    }

}
