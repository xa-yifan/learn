package org.efan.filter;

import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * WebFilter 父类
 *
 * @author xiaofan
 */
public abstract class AbstractWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return doFilter(exchange, chain).switchIfEmpty(Mono.just(false))
                .flatMap(filterResult -> filterResult ? chain.filter(exchange) : doDenyResponse(exchange));
    }

    protected abstract Mono<Boolean> doFilter(ServerWebExchange exchange, WebFilterChain chain);

    protected abstract Mono<Void> doDenyResponse(ServerWebExchange exchange);

}
