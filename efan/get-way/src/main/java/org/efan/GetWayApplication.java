package org.efan;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;


/**
 * @author liuf
 * @date 2020/3/4 11:41
 */

@SpringBootApplication
@EnableWebFlux
public class GetWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GetWayApplication.class, args);
    }
}
