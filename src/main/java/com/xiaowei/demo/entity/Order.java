package com.xiaowei.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("`order`")
public class Order extends BaseEntity {

    @TableField("order_no")
    private String orderNo;

    @TableField("user_id")
    private String userId;

    @TableField("activity_id")
    private String activityId;

    @TableField("product_id")
    private String productId;

    private Integer quantity;

    @TableField("unit_price")
    private BigDecimal unitPrice;

    @TableField("total_amount")
    private BigDecimal totalAmount;

    @TableField("payment_amount")
    private BigDecimal paymentAmount;

    private Integer status = 0; // 0-待支付，1-已支付，2-已发货，3-已完成，4-已取消，5-退款中，6-已退款

    @TableField("payment_method")
    private String paymentMethod;

    @TableField("payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    @TableField("shipping_address")
    private String shippingAddress; // JSON格式存储

    private String remark;

    // 关联查询字段
    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private Product product;

    @TableField(exist = false)
    private GroupActivity groupActivity;

    // 扩展字段：解析后的收货地址
    @TableField(exist = false)
    private ShippingAddress parsedShippingAddress;

    // 扩展字段：是否已评价
    @TableField(exist = false)
    private Boolean reviewed = false;

    /**
     * 获取状态描述
     */
    public String getStatusText() {
        switch (this.status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已发货";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "退款中";
            case 6: return "已退款";
            default: return "未知状态";
        }
    }

    /**
     * 检查订单是否可取消
     */
    public boolean canCancel() {
        return this.status == 0 || this.status == 1; // 待支付或已支付
    }

    /**
     * 检查订单是否可支付
     */
    public boolean canPay() {
        return this.status == 0; // 待支付
    }

    /**
     * 检查订单是否可确认收货
     */
    public boolean canConfirmReceipt() {
        return this.status == 2; // 已发货
    }

    /**
     * 检查订单是否可申请退款
     */
    public boolean canRefund() {
        return this.status == 1; // 已支付
    }

    /**
     * 解析收货地址JSON
     */
    public ShippingAddress getParsedShippingAddress() {
        if (this.shippingAddress == null || this.shippingAddress.trim().isEmpty()) {
            return null;
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            ShippingAddress address = objectMapper.readValue(this.shippingAddress, ShippingAddress.class);
            return address;
        } catch (Exception e) {
            // 如果解析失败，尝试从字符串解析
            return parseShippingAddressFromString(this.shippingAddress);
        }
    }

    /**
     * 设置收货地址
     */
    public void setShippingAddress(ShippingAddress address) {
        if (address == null) {
            this.shippingAddress = null;
            return;
        }

        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            this.shippingAddress = objectMapper.writeValueAsString(address);
        } catch (Exception e) {
            // 如果序列化失败，使用简单格式
            this.shippingAddress = address.toString();
        }
    }

    /**
     * 从字符串解析收货地址（兼容旧格式）
     */
    private ShippingAddress parseShippingAddressFromString(String addressStr) {
        if (addressStr == null || addressStr.trim().isEmpty()) {
            return null;
        }

        try {
            // 尝试解析JSON格式
            if (addressStr.startsWith("{") && addressStr.endsWith("}")) {
                com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
                return objectMapper.readValue(addressStr, ShippingAddress.class);
            }

            // 如果是简单字符串格式，创建基本对象
            ShippingAddress address = new ShippingAddress();
            address.setDetailAddress(addressStr);
            return address;
        } catch (Exception e) {
            // 创建默认对象
            ShippingAddress address = new ShippingAddress();
            address.setDetailAddress(addressStr);
            return address;
        }
    }

    /**
     * 内部类：收货地址
     */
    @Data
    public static class ShippingAddress {
        private String receiverName;
        private String receiverPhone;
        private String province;
        private String city;
        private String district;
        private String detailAddress;
        private Boolean isDefault = false;

        @Override
        public String toString() {
            return String.format("%s %s %s%s%s %s",
                    receiverName, receiverPhone,
                    province, city, district, detailAddress);
        }

        /**
         * 获取完整地址
         */
        public String getFullAddress() {
            StringBuilder sb = new StringBuilder();
            if (province != null) sb.append(province);
            if (city != null) sb.append(city);
            if (district != null) sb.append(district);
            if (detailAddress != null) sb.append(detailAddress);
            return sb.toString();
        }

        /**
         * 获取简短地址（隐藏手机号中间4位）
         */
        public String getShortAddress() {
            String phone = receiverPhone;
            if (phone != null && phone.length() == 11) {
                phone = phone.substring(0, 3) + "****" + phone.substring(7);
            }

            StringBuilder sb = new StringBuilder();
            if (receiverName != null) sb.append(receiverName).append(" ");
            if (phone != null) sb.append(phone).append("\n");

            String address = getFullAddress();
            if (address.length() > 30) {
                address = address.substring(0, 30) + "...";
            }
            sb.append(address);

            return sb.toString();
        }
    }
}
