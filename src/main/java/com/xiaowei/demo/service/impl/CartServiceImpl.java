package com.xiaowei.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowei.demo.entity.Cart;
import com.xiaowei.demo.mapper.CartMapper;
import com.xiaowei.demo.service.CartService;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

}
