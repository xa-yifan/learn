package org.efan.filter.support;

import org.efan.common.common.EfanConstants;
import org.efan.filter.AbstractWebFilter;
import org.efan.result.EfanResult;
import org.efan.result.EfanResultUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class SignWebFilter extends AbstractWebFilter {
    @Override
    protected Mono<Boolean> doFilter(ServerWebExchange exchange, WebFilterChain chain) {
        //验签
        return Mono.just(true);
    }

    @Override
    protected Mono<Void> doDenyResponse(ServerWebExchange exchange) {

        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return EfanResultUtils.result(exchange, EfanResult.returnVaule(EfanConstants.Code.SIGN_IS_NOT_PASS));
    }
}
