package com.xiaowei.demo.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aop.debug")
public class AopDebugProperties {
    private Boolean controller = true;
    private Boolean service = false;
    private Boolean logRecord = true;
}
