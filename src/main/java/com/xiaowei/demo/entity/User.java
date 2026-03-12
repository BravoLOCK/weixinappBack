package com.xiaowei.demo.entity;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {

    @TableId(type = IdType.ASSIGN_UUID)  // 改为UUID生成策略
    private String id;  // 从 Long 改为 String

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("avatar")
    private String avatar;

    @TableField("name")
    private String name;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("age")
    private Integer age;

    @TableField("gender")
    private Integer gender;

    @TableField("user_type")
    private Integer userType;

    @TableField("status")
    private Integer status;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    // 性别枚举
    public static class Gender {
        public static final int UNKNOWN = 0;
        public static final int MALE = 1;
        public static final int FEMALE = 2;
    }

    // 状态枚举
    public static class Status {
        public static final int DISABLED = 0;
        public static final int ENABLED = 1;
    }

    /**
     * 重写set方法，增加MD5加密功能
     * @param password
     */
    public void setPassword(String password) {
        if(password != null && !"".equalsIgnoreCase(password) && password.length() <= 20) {
            password = DigestUtil.md5Hex(password);
        }

        this.password = password;
    }

}
