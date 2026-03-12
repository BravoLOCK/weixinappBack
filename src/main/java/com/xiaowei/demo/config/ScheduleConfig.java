package com.xiaowei.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduleConfig {

    // 每天凌晨1点处理过期的团购活动
    @Scheduled(cron = "0 0 1 * * ?")
    public void handleExpiredGroupActivities() {
        // 注入IGroupActivityService并调用handleExpiredActivities()
    }

    // 每5分钟检查并更新团购状态
    @Scheduled(fixedRate = 300000)
    public void checkGroupActivityStatus() {
        // 检查进行中的团购是否已成团，成团的更新状态并发货，不成团的关闭此团购并退款
        log.debug("checkGroupActivityStatus......");
    }
}
