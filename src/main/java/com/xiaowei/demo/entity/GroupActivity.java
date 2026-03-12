package com.xiaowei.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("group_activity")
public class GroupActivity extends BaseEntity {

    @TableField("product_id")
    private String productId;

    private String title;

    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @TableField("limit_per_user")
    private Integer limitPerUser = 1;

    @TableField("current_members")
    private Integer currentMembers = 1;

    @TableField("target_members")
    private Integer targetMembers;

    @TableField("leader_id")
    private String leaderId;

    private Integer status = 0; // 0-未开始，1-进行中，2-已成团，3-已结束，4-团购失败

    // 扩展字段，用于存储团购价（如果需要独立于商品价格）
    @TableField(exist = false)
    private BigDecimal groupPrice;

    // 商品信息（关联查询）
    @TableField(exist = false)
    private Product product;

    // 团购进度
    @TableField(exist = false)
    private Double progress;

    // 剩余时间（小时）
    @TableField(exist = false)
    private Long remainingHours;

    // 是否可参与
    @TableField(exist = false)
    private Boolean canJoin;

    // 团购成员列表
    @TableField(exist = false)
    private java.util.List<GroupMember> members;

    // 团长
    @TableField(exist = false)
    private User leader;

    // 购买数量
    @TableField(exist = false)
    private Integer quantity = 1;

    /**
     * 获取状态描述
     */
    public String getStatusText() {
        switch (this.status) {
            case 0: return "未开始";
            case 1: return "进行中";
            case 2: return "已成团";
            case 3: return "已结束";
            case 4: return "团购失败";
            default: return "未知状态";
        }
    }

    /**
     * 检查活动是否进行中
     */
    public boolean isInProgress() {
        if (this.status != 1) return false;
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(this.startTime) && now.isBefore(this.endTime);
    }

    /**
     * 检查活动是否已结束
     */
    public boolean isEnded() {
        if (this.status == 3 || this.status == 4) return true;
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(this.endTime);
    }

    /**
     * 检查活动是否可加入
     */
    public boolean canJoin() {
        if (!isInProgress()) return false;
        return this.currentMembers < this.targetMembers;
    }

    /**
     * 设置剩余时间（小时）
     */
    public void setRemainingHours() {
        if (isEnded()) {
            this.remainingHours = 0L;
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        this.remainingHours = java.time.Duration.between(now, this.endTime).getSeconds() / 3600;
    }

    /**
     * 获取剩余时间（小时）
     */
    public long getRemainingSeconds() {
        if (isEnded()) return 0;
        LocalDateTime now = LocalDateTime.now();
        return java.time.Duration.between(now, this.endTime).getSeconds()/3600;
    }

    /**
     * 获取团购进度（0-100）
     */
    public double getProgressPercentage() {
        if (this.targetMembers == 0) return 0;
        double progress = (double) this.currentMembers / this.targetMembers * 100;
        return Math.min(progress, 100.0);
    }
}