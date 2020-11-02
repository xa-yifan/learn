package org.efan.client.apache.dubbo.configuration;

import org.efan.client.apache.dubbo.common.ZkClient;
import org.efan.client.apache.dubbo.config.DubboConfig;
import org.efan.client.apache.dubbo.spring.DubboServiceBeanPostProcessor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * 让Spring管理起来
 * @author xiaofan
 */
@Configuration
@EnableConfigurationProperties(DubboConfig.class)
public class DubboClientAutoConfiguration {

    private final DubboConfig dubboConfig;

    public DubboClientAutoConfiguration(final DubboConfig dubboConfig) {
        this.dubboConfig = dubboConfig;
    }

    /**
     * 注册zookeeper客户端
     * @return
     */
    @Bean(initMethod = "init")
    public ZkClient zkCustor() {
        return new ZkClient(dubboConfig.getZookeeperUrl());
    }

    /**
     * 往zookeeper注册数据
     * @return
     */
    @Bean
    public DubboServiceBeanPostProcessor dubboServiceBeanPostProcessor() {
        return new DubboServiceBeanPostProcessor(dubboConfig,zkCustor());
    }

}
