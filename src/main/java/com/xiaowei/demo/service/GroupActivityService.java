package com.xiaowei.demo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xiaowei.demo.entity.GroupActivity;

public interface GroupActivityService extends IService<GroupActivity> {

    boolean createGroupActivity(GroupActivity groupActivity);

    boolean joinGroupActivity(String activityId, String userId, Integer quantity);

}
