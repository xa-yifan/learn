
package org.efan.config;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.model.ApplicationModel;
import org.apache.dubbo.rpc.service.GenericService;
import org.efan.common.vo.MetaData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Optional;

import static org.efan.common.common.EfanConstants.Loadbalance.*;

/**
 * 泛化缓存
 * @author xiaofan
 */
@SuppressWarnings("all")
public final class ApplicationConfigCache {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationConfigCache.class);

    private ApplicationConfig applicationConfig;

    private RegistryConfig registryConfig;
    private final int maxCount = 500;
    private final Cache<String, ReferenceConfig<GenericService>> cache = Caffeine.newBuilder()
            .maximumSize(maxCount)
            .build();
    private ApplicationConfigCache() {
    }

    /**
     * 获取ApplicationConfigCache对象.
     *
     * @return 对象 instance
     */
    public static ApplicationConfigCache getInstance() {
        return ApplicationConfigCacheInstance.INSTANCE;
    }

    public void init(final String register) {
        invalidateAll();
        if (applicationConfig == null) {
            applicationConfig = new ApplicationConfig("efan");
        }
        if (registryConfig == null) {
            registryConfig = new RegistryConfig();
            registryConfig.setProtocol("dubbo");
            registryConfig.setId("efan");
            registryConfig.setRegister(false);
            registryConfig.setAddress("zookeeper://"+register);
        }
    }

    /**
     * 根据metaData获取缓存泛化调用对象
     *
     * @param metaData the meta data
     * @return the reference config
     */
    public ReferenceConfig<GenericService> initRef(final MetaData metaData) {
        try {
            ReferenceConfig<GenericService> referenceConfig = cache.getIfPresent(metaData.getServiceName());
            if(null!=referenceConfig){
                if (StringUtils.isNoneBlank(referenceConfig.getInterface())) {
                    return referenceConfig;
                }
            }

        } catch (Exception e) {
            LOG.error(" [EFAN] init dubbo ref ex:{}", e.getMessage());
        }
        return build(metaData);

    }

    public ReferenceConfig<GenericService> build(final MetaData metaData) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setGeneric("true");
        reference.setRegistry(registryConfig);
        reference.setInterface(metaData.getServiceName());
        reference.setApplication(applicationConfig);
        reference.setProtocol("dubbo");
        String rpcExt = metaData.getRpcExt();
        try {
            MetaData.RpcExt dubboParamExt = JSON.parseObject(rpcExt, MetaData.RpcExt.class);
            //默认负载均衡策略
            reference.setLoadbalance(RANDOM);
            if (Objects.nonNull(dubboParamExt)) {
                if (StringUtils.isNoneBlank(dubboParamExt.getVersion())) {
                    reference.setVersion(dubboParamExt.getVersion());
                }
                if (StringUtils.isNoneBlank(dubboParamExt.getGroup())) {
                    reference.setGroup(dubboParamExt.getGroup());
                }
                if (StringUtils.isNoneBlank(dubboParamExt.getLoadbalance())) {
                    final String loadBalance = dubboParamExt.getLoadbalance();
                    if (HASH.equals(loadBalance) || CONSISTENT_HASH.equals(loadBalance)) {
                        reference.setLoadbalance(CONSISTENT_HASH);
                    } else if (ROUND_ROBIN.equals(loadBalance)) {
                        reference.setLoadbalance(ROUND_ROBIN);
                    } else {
                        reference.setLoadbalance(loadBalance);
                    }
                }
                Optional.ofNullable(dubboParamExt.getTimeout()).ifPresent(reference::setTimeout);
                Optional.ofNullable(dubboParamExt.getRetries()).ifPresent(reference::setRetries);
            }
        } catch (Exception e) {
            LOG.error(" [EFAN] rpc 扩展参数转成json异常,{}", metaData);
        }
        try {
            reference.setLazy(true);
            GenericService obj = reference.get();
            if (obj != null) {
                LOG.info(" [EFAN] 初始化引用成功{}", metaData);
                cache.put(metaData.getServiceName(), reference);
            }
        } catch (Exception ex) {
            LOG.error(" [EFAN] 初始化引用没有找到提供者【{}】,ex:{}", metaData, ex.getMessage());
        }
        return reference;
    }

    public ReferenceConfig<GenericService> get(final String serviceName) {
        return cache.getIfPresent(serviceName);
    }

    public void invalidate(final String serviceName) {
//        CONSUMED_SERVICES.remove(serviceName);
        ApplicationModel.reset();
        cache.invalidate(serviceName);
    }


    public void invalidateAll() {
        cache.invalidateAll();
    }


    static class ApplicationConfigCacheInstance {

        static final ApplicationConfigCache INSTANCE = new ApplicationConfigCache();
    }

}
