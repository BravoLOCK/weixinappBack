package com.xiaowei.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xiaowei.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password} AND status = 1")
    User selectByUsername(String username, String password);

}