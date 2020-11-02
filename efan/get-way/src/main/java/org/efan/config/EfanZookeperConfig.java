package org.efan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author liuf
 * @date 2020/3/5 17:33
 */
@ConfigurationProperties(prefix = "efan.zookeper")
@Data
public class EfanZookeperConfig {
    private String url;
    private Integer port;
}
