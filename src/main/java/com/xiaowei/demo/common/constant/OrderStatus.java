package com.xiaowei.demo.common.constant;

/**
 * 订单状态常量
 */
public interface OrderStatus {
    int PENDING_PAYMENT = 0;      // 待支付
    int PAID = 1;                 // 已支付
    int SHIPPED = 2;              // 已发货
    int COMPLETED = 3;            // 已完成
    int CANCELLED = 4;            // 已取消
    int REFUNDING = 5;            // 退款中
    int REFUNDED = 6;             // 已退款
}
