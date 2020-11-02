package org.efan.client.apache.dubbo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * zookeeper配置和接口前缀和应用名称
 * @author xiaofan
 */
@ConfigurationProperties(prefix = "efan.dubbo")
@Data
public class DubboConfig {
    /**
     * 注册中心地址 127.0.0.1:2181
     */
    private String zookeeperUrl;
    /**
     * 接口地址
     */
    private String contextPath;

    /**
     * 应用名称 不写默认取Application.name
     */
    private String appName;
}
