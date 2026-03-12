package com.xiaowei.demo.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaowei.demo.entity.Product;

public interface ProductService extends IService<Product> {

    boolean updateStock(String productId, Integer quantity);

    boolean updateSales(String productId, Integer quantity);

}
