package com.xiaowei.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("cart")
public class Cart extends BaseEntity {

    @TableField("user_id")
    private String userId;

    @TableField("product_id")
    private String productId;

    @TableField("activity_id")
    private String activityId;

    @TableField("quantity")
    private Integer quantity;

    @TableField("selected")
    private Integer selected = 1; // 0-未选中，1-已选中

    // 关联查询字段
    @TableField(exist = false)
    private Product product;

    @TableField(exist = false)
    private GroupActivity activity;

    // 扩展字段：是否可用
    @TableField(exist = false)
    private Boolean available = true;

    // 扩展字段：商品库存状态
    @TableField(exist = false)
    private Integer stockStatus;

    // 扩展字段：商品价格
    @TableField(exist = false)
    private BigDecimal price;

    /**
     * 获取商品单价
     */
    public BigDecimal getUnitPrice() {
        if (this.activity != null && this.activity.getGroupPrice() != null) {
            return this.activity.getGroupPrice();
        }
        if (this.product != null && this.product.getGroupPrice() != null) {
            return this.product.getGroupPrice();
        }
        return BigDecimal.ZERO;
    }

    /**
     * 计算小计
     */
    public BigDecimal getSubtotal() {
        if (this.quantity == null || this.quantity <= 0) {
            return BigDecimal.ZERO;
        }
        return getUnitPrice().multiply(new BigDecimal(this.quantity));
    }

    /**
     * 检查商品是否可用
     */
    public boolean isAvailable() {
        if (this.product == null) {
            return false;
        }

        // 检查商品状态
        if (this.product.getStatus() != 1) { // 非上架状态
            return false;
        }

        // 检查库存
        if (this.product.getStock() < this.quantity) {
            return false;
        }

        // 如果关联了团购活动，检查活动状态
        if (this.activity != null && this.activity.getStatus() != 1) {
            return false;
        }

        return true;
    }

    /**
     * 获取商品库存状态描述
     */
    public String getStockStatusText() {
        if (this.product == null) {
            return "商品不存在";
        }

        if (this.product.getStock() <= 0) {
            return "无货";
        }

        if (this.product.getStock() < this.quantity) {
            return "库存不足";
        }

        return "有货";
    }

    /**
     * 获取购物车项显示名称
     */
    public String getDisplayName() {
        if (this.product != null) {
            return this.product.getName();
        }
        return "未知商品";
    }

    /**
     * 获取购物车项图片
     */
    public String getDisplayImage() {
        if (this.product != null && this.product.getMainImage() != null) {
            return this.product.getMainImage();
        }
        return "";
    }
}
