package com.xiaowei.demo.common.constant;

/**
 * 团购活动状态常量
 */
public interface GroupActivityStatus {
    int NOT_STARTED = 0;          // 未开始
    int IN_PROGRESS = 1;          // 进行中
    int SUCCESS = 2;              // 已成团
    int ENDED = 3;                // 已结束
    int FAILED = 4;               // 团购失败
}
