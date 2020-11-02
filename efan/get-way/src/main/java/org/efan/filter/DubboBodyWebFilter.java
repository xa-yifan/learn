
package org.efan.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.efan.common.common.EfanConstants;
import org.efan.common.vo.RequestVO;
import org.efan.filter.AbstractWebFilter;
import org.efan.result.EfanResult;
import org.efan.result.EfanResultUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

import static org.efan.common.common.EfanConstants.DUBBO_PARAMS;
import static org.efan.common.common.EfanConstants.REQUEST_VO;

/**
 * 获取 application/json 请求参数 存于exchange-attributes-dubbo_params
 *
 * @author xiaofan
 */
@Slf4j
public class DubboBodyWebFilter implements WebFilter {

    private final List<HttpMessageReader<?>> messageReaders;

    public DubboBodyWebFilter() {
        this.messageReaders = HandlerStrategies.withDefaults().messageReaders();
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final RequestVO requestDTO = exchange.getAttribute(REQUEST_VO);
        if (Objects.nonNull(requestDTO)) {
            ServerRequest serverRequest = ServerRequest.create(exchange, messageReaders);
            return serverRequest.bodyToMono(String.class).flatMap(body->{
                if(StringUtils.isNotBlank(body)){
                    exchange.getAttributes().put(DUBBO_PARAMS, body);
                }else{
                    log.error("body is null");
                }
                return chain.filter(exchange);
            });
        }
        return chain.filter(exchange);
    }
}
