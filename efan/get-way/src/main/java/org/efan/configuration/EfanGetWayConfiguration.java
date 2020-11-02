package org.efan.configuration;

import org.efan.common.client.ZkClient;
import org.efan.config.EfanZookeperConfig;
import org.efan.config.InitMetaData;
import org.efan.filter.*;
import org.efan.filter.DubboBodyWebFilter;
import org.efan.filter.DubboReponseWebFilter;
import org.efan.filter.support.ParamWebFilter;
import org.efan.filter.support.TimeWebFilter;
import org.efan.service.DubboProxyService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.WebFilter;

/**
 * @author liuf
 * @date 2020/3/5 17:26
 */
@Configuration
@EnableConfigurationProperties(EfanZookeperConfig.class)
public class EfanGetWayConfiguration {

    private final EfanZookeperConfig efanZookeperConfig;

    public EfanGetWayConfiguration(EfanZookeperConfig efanZookeperConfig) {
        this.efanZookeperConfig = efanZookeperConfig;
    }

    /**
     * 注册zookeeper客户端
     *
     * @return ZkClient
     */
    @Bean(initMethod = "init")
    @Order(-50)
    public ZkClient zkCustor() {
        return new ZkClient(efanZookeperConfig.getUrl());
    }

    /**
     * 初始化dubbo提供者
     * @return @InitMetaData
     */
    @Bean(initMethod = "init")
    @Order(-10)
    public InitMetaData initMetaData() {
        return new InitMetaData(zkCustor(), efanZookeperConfig.getUrl());
    }

    /**
     * 跨域
     * @return
     */
    @Bean
    @Order(-100)
    public WebFilter crossFilter() {
        return new CrossFilter();
    }
    @Bean
    @Order(-1)
    public WebFilter paramWebFilter(){
        return new ParamWebFilter();
    }

    /**
     * 时间戳
     * @return
     */
    @Bean
    @Order(0)
    public WebFilter timeFilter(){
        return new TimeWebFilter();
    }
    /**
     * 泛化调用dubbo
     * @return
     */
    @Bean
    public DubboProxyService dubboProxyService() {
        return new DubboProxyService();
    }

    /**
     * 构建dubbo body
     * @return
     */
    @Bean
    @Order(1)
    public WebFilter dubboBodyFilter(){
        return new DubboBodyWebFilter();
    }

    /**
     * dubbo返回
     * @return
     */
    @Bean
    @Order(5)
    public WebFilter dubboReponse() {
        return new DubboReponseWebFilter(dubboProxyService());
    }


}
