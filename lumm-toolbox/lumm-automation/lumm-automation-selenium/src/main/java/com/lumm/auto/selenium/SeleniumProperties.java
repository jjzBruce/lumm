package com.lumm.auto.selenium;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "integration.selenium")
public class SeleniumProperties {

    /**
     * 驱动地址
     */
    String drivePath;


}
