package com.xiaowei.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaowei.demo.entity.User;
import java.util.List;

public interface UserService extends IService<User> {

    List<User> queryUsers(String keyword);

    User login(User user);

}
