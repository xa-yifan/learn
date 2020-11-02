
package org.efan.filter.support;

import org.efan.common.utils.DateUtils;
import org.efan.common.vo.RequestVO;
import org.efan.filter.AbstractWebFilter;
import org.efan.result.EfanResult;
import org.efan.result.EfanResultUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

import static org.efan.common.common.EfanConstants.Code.TIME_ERROR;
import static org.efan.common.common.EfanConstants.REQUEST_VO;
import static org.efan.common.common.EfanConstants.Time.DELAY;
/**
 *　　　　　　__
 *  　___  / _|  __ _  _ __
 * 　/ _ \| |_  / _` || '_ \
 *　|  __/|  _|| (_| || | | |
 * 　\___||_|   \__,_||_| |_|
 * @author  fan
 * @date  2020-3-20 16:01
 * @description: TODO : 判断请求时间过滤器
 */
public class TimeWebFilter extends AbstractWebFilter {

    @Override
    protected Mono<Boolean> doFilter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final RequestVO requestDTO = exchange.getAttribute(REQUEST_VO);
        if (Objects.isNull(requestDTO) || Objects.isNull(requestDTO.getStartDateTime())) {
            return Mono.just(false);
        }
        final LocalDateTime start = requestDTO.getStartDateTime();
        final LocalDateTime now = LocalDateTime.now();
        final long between = DateUtils.acquireMinutesBetween(start, now);
        System.out.println(between);
        if (between < DELAY) {
            return Mono.just(true);
        }
        return Mono.just(false);
    }

    @Override
    protected Mono<Void> doDenyResponse(final ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.REQUEST_TIMEOUT);
        return EfanResultUtils.result(exchange, EfanResult.returnVaule(TIME_ERROR));
    }
}
