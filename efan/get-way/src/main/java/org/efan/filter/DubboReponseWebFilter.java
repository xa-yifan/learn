package org.efan.filter;

import org.efan.common.vo.RequestVO;
import org.efan.result.EfanResult;
import org.efan.result.EfanResultUtils;
import org.efan.service.DubboProxyService;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.efan.common.common.EfanConstants.*;


/**
 * @author liuf
 * @date 2020/3/5 18:46
 */
public class DubboReponseWebFilter implements WebFilter {
    private final DubboProxyService dubboProxyService;
    public DubboReponseWebFilter(DubboProxyService dubboProxyService) {
        this.dubboProxyService=dubboProxyService;
    }


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        final String body = exchange.getAttribute(DUBBO_PARAMS);
        final RequestVO requestDTO = exchange.getAttribute(REQUEST_VO);
        final Object res;
        try {
            res = dubboProxyService.genericInvoker(body, requestDTO.getMetaData());
        } catch (Exception e) {
            e.printStackTrace();
            return EfanResultUtils.result(exchange,EfanResult.returnVaule(Code.FAIL));
        }
        if(null==res){
            return EfanResultUtils.result(exchange,EfanResult.returnVaule(Code.FAIL));
        }
        removeClass(res);

        //加签

        return EfanResultUtils.result(exchange,res);

    }



    private Object removeClass(final Object object) {
        if (object instanceof Map) {
            Map map = (Map) object;
            Object result = map.get("result");
            if (result instanceof Map) {
                Map resultMap = (Map) result;
                resultMap.remove("class");
            }
            map.remove("class");
            return object;
        } else {
            return object;
        }
    }
}
