package com.xiaowei.demo.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xiaowei.demo.entity.GroupMember;
import com.xiaowei.demo.mapper.GroupMemberMapper;
import com.xiaowei.demo.service.GroupMemberService;
import org.springframework.stereotype.Service;

@Service
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {

}
