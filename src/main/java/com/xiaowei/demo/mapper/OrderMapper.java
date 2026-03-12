package com.xiaowei.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowei.demo.entity.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

}