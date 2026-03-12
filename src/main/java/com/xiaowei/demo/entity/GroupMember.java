package com.xiaowei.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("group_member")
public class GroupMember extends BaseEntity {

    @TableField("group_activity_id")
    private String groupActivityId;

    @TableField("user_id")
    private String userId;

    @TableField("order_id")
    private String orderId;

    @TableField("is_leader")
    private Integer isLeader = 0; // 0-否，1-是

    @TableField("join_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime joinTime;

    // 关联查询字段
    @TableField(exist = false)
    private User user;

    @TableField(exist = false)
    private Order order;

    @TableField(exist = false)
    private GroupActivity groupActivity;

    /**
     * 是否为团长
     */
    public boolean isLeader() {
        return this.isLeader != null && this.isLeader == 1;
    }

    /**
     * 设置是否为团长
     */
    public void setLeader(boolean leader) {
        this.isLeader = leader ? 1 : 0;
    }

    /**
     * 获取加入时间描述
     */
    public String getJoinTimeText() {
        if (this.joinTime == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();
        long minutes = java.time.Duration.between(this.joinTime, now).toMinutes();

        if (minutes < 1) {
            return "刚刚";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (minutes < 24 * 60) {
            long hours = minutes / 60;
            return hours + "小时前";
        } else {
            long days = minutes / (24 * 60);
            return days + "天前";
        }
    }

    /**
     * 检查是否已支付
     */
    public boolean isPaid() {
        if (this.order == null) {
            return false;
        }
        return this.order.getStatus() >= 1; // 1-已支付及以上状态
    }
}