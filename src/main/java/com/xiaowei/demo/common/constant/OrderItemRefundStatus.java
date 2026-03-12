package com.xiaowei.demo.common.constant;

/**
 * 订单项退款状态常量
 */
public interface OrderItemRefundStatus {
    int NO_REFUND = 0;            // 无退款
    int REFUNDING = 1;            // 退款中
    int REFUNDED = 2;             // 已退款
}