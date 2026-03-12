package com.xiaowei.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowei.demo.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

}