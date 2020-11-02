
package org.efan.filter.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.efan.common.common.EfanConstants;
import org.efan.common.utils.DateUtils;
import org.efan.common.vo.MetaData;
import org.efan.common.vo.RequestVO;
import org.efan.filter.AbstractWebFilter;
import org.efan.result.EfanResult;
import org.efan.result.EfanResultUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.efan.common.common.EfanConstants.*;
import static org.efan.config.DataConfig.META_DATA;


/**
 * 转换请求参数为需要的类型
 * @author fan
 */
@Slf4j
public class ParamWebFilter extends AbstractWebFilter {

    @Override
    protected Mono<Boolean> doFilter(final ServerWebExchange exchange, final WebFilterChain chain) {
        final ServerHttpRequest request = exchange.getRequest();
        final HttpHeaders headers = request.getHeaders();
        if(!MediaType.APPLICATION_JSON.isCompatibleWith(headers.getContentType())){
            //请求类型不对
            log.error("ContentType类型错误");
            return Mono.just(false);
        }

        return doParam(exchange);
    }

    @Override
    protected Mono<Void> doDenyResponse(final ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        return EfanResultUtils.result(exchange, EfanResult.returnVaule(EfanConstants.Code.PARAM_ERROR));
    }

    public Mono<Boolean> doParam(final ServerWebExchange exchange) {
        final ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        //在本地缓存获取
        MetaData metaData = META_DATA.get(path);
        if (Objects.isNull(metaData) || !metaData.isEnabled()) {
            log.error("metaData is not");
            return Mono.just(false);
        }
        final String appId = request.getHeaders().getFirst(APP_ID);
        final String sign = request.getHeaders().getFirst(SIGN);
        final String timestamp = request.getHeaders().getFirst(TIME_TAMP);
        if (StringUtils.isAnyBlank(appId,sign,timestamp)) {
            log.error("Heard appId or sign or timestamp is not ");
            return Mono.just(false);
        }
        RequestVO requestVO = transform(path,appId,sign,Long.valueOf(timestamp),metaData);
        if (!verify(requestVO)) {
            log.error("requestVO is not ");
            return Mono.just(false);
        }
        exchange.getAttributes().put(REQUEST_VO, requestVO);
        return Mono.just(true);
    }

    private RequestVO transform(String path,String appId,String sign,long timestamp, final MetaData metaData) {
        RequestVO requestVO = new RequestVO();
        requestVO.setPath(path);
        requestVO.setModule(metaData.getAppName());
        requestVO.setMethod(metaData.getServiceName());
        requestVO.setAppId(appId);
        requestVO.setSign(sign);
        requestVO.setMetaData(metaData);
        requestVO.setStartDateTime(DateUtils.formatLocalDateTimeFromTimestamp(timestamp));
        return requestVO;
    }

    private Boolean verify(final RequestVO requestVO) {
        return !Objects.isNull(requestVO)
                && !StringUtils.isBlank(requestVO.getModule())
                && !StringUtils.isBlank(requestVO.getMethod());
    }
}
