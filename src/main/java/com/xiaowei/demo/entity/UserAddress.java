package com.xiaowei.demo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_address")
public class UserAddress extends BaseEntity {

    @TableField("user_id")
    private String userId;

    @TableField("receiver_name")
    private String receiverName;

    @TableField("receiver_phone")
    private String receiverPhone;

    private String province;

    private String city;

    private String district;

    @TableField("detail_address")
    private String detailAddress;

    @TableField("is_default")
    private Integer isDefault = 0; // 0-否，1-是

    // 关联查询字段
    @TableField(exist = false)
    private User user;

    /**
     * 是否为默认地址
     */
    public boolean isDefault() {
        return this.isDefault != null && this.isDefault == 1;
    }

    /**
     * 设置是否为默认地址
     */
    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault ? 1 : 0;
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

    /**
     * 获取收货人信息
     */
    public String getReceiverInfo() {
        String phone = receiverPhone;
        if (phone != null && phone.length() == 11) {
            phone = phone.substring(0, 3) + "****" + phone.substring(7);
        }
        return receiverName + " " + phone;
    }

    /**
     * 验证地址信息是否完整
     */
    public boolean isValid() {
        return receiverName != null && !receiverName.trim().isEmpty() &&
                receiverPhone != null && !receiverPhone.trim().isEmpty() &&
                province != null && !province.trim().isEmpty() &&
                city != null && !city.trim().isEmpty() &&
                detailAddress != null && !detailAddress.trim().isEmpty();
    }
}
