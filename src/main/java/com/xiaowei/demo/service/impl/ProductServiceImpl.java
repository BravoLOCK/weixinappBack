package com.xiaowei.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowei.demo.entity.Product;
import com.xiaowei.demo.mapper.ProductMapper;
import com.xiaowei.demo.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {


    @Override
    public boolean updateStock(String productId, Integer quantity) {
        Product product = baseMapper.selectById(productId);
        if (product == null || product.getStock() < quantity) {
            return false;
        }

        product.setStock(product.getStock() - quantity);
        return updateById(product);
    }

    @Override
    public boolean updateSales(String productId, Integer quantity) {
        Product product = baseMapper.selectById(productId);
        if (product == null) {
            return false;
        }

        product.setSales(product.getSales() + quantity);
        return updateById(product);
    }
}
