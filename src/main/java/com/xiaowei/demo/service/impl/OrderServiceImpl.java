package com.xiaowei.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowei.demo.entity.Order;
import com.xiaowei.demo.mapper.OrderMapper;
import com.xiaowei.demo.service.OrderService;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

}
