
package org.efan.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.rpc.service.GenericException;
import org.apache.dubbo.rpc.service.GenericService;
import org.efan.common.vo.MetaData;
import org.efan.config.ApplicationConfigCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

/**
 * 泛化调用类
 *
 * @author xiaofan
 */
public class DubboProxyService {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DubboProxyService.class);


    /**
     * Generic invoker object.
     *
     * @param body     the body
     * @param metaData the meta data
     * @return the object
     */
    public Object genericInvoker(final String body, final MetaData metaData) {
        ReferenceConfig<GenericService> reference;
        GenericService genericService;
        try {
            reference = ApplicationConfigCache.getInstance().get(metaData.getServiceName());
            if (Objects.isNull(reference) || StringUtils.isEmpty(reference.getInterface())) {
                ApplicationConfigCache.getInstance().invalidate(metaData.getServiceName());
                reference = ApplicationConfigCache.getInstance().initRef(metaData);
            }
            genericService = reference.get();
        } catch (Exception ex) {
            LOGGER.error("dubbo 泛化初始化异常:", ex);
            ApplicationConfigCache.getInstance().invalidate(metaData.getServiceName());
            reference = ApplicationConfigCache.getInstance().initRef(metaData);
            genericService = reference.get();
        }
        try {
            if ("".equals(body) || "{}".equals(body) || "null".equals(body)) {
                return genericService.$invoke(metaData.getMethodName(), new String[]{}, new Object[]{});
            } else {
                Map<String, Object> paramMap= JSON.parseObject(body,new TypeReference<Map<String, Object>>(){});
                return genericService.$invoke(metaData.getMethodName(),
                        new String[]{metaData.getParameterTypes().replaceAll("\\[","")}, new Object[]{paramMap});
            }
        } catch (GenericException e) {
            LOGGER.error("dubbo 泛化调用异常", e);
            return null;
        }
    }

}
