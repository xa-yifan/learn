package org.efan.client.apache.dubbo.spring;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.spring.ServiceBean;
import org.efan.client.apache.dubbo.common.EfanClient;
import org.efan.client.apache.dubbo.common.ZkClient;
import org.efan.client.apache.dubbo.config.DubboConfig;
import org.efan.client.apache.dubbo.vo.MetaDataVO;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The type Dubbo listener.
 *
 * @author xiaofan
 */
@Slf4j
public class DubboServiceBeanPostProcessor implements ApplicationListener<ApplicationReadyEvent> {

    private DubboConfig dubboConfig;
    private ZkClient zkClient;

    private ExecutorService executorService = new ThreadPoolExecutor(
            1,1,0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("efan-thread-call-runner-%d").build());

    public DubboServiceBeanPostProcessor(final DubboConfig dubboConfig, final ZkClient zkClient) {
        this.dubboConfig = dubboConfig;
        this.zkClient=zkClient;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        event.getApplicationContext().getBeansOfType(ServiceBean.class)
                .forEach((e,t)->executorService.execute(() -> handler(t)));
    }
    private void handler(final ServiceBean<?> serviceBean) {
        Class<?> clazz = serviceBean.getRef().getClass();
        if (clazz.getName().contains(ClassUtils.CGLIB_CLASS_SEPARATOR)) {
            String superClassName = clazz.getGenericSuperclass().getTypeName();
            try {
                clazz = Class.forName(superClassName);
            } catch (ClassNotFoundException e) {
                log.error(String.format(" [EFAN] class not found: %s", superClassName));
                return;
            }
        }
        String contextPath = dubboConfig.getContextPath();
        String zookeeperUrl = dubboConfig.getZookeeperUrl();
        if (contextPath == null || "".equals(contextPath)
                || zookeeperUrl == null || "".equals(zookeeperUrl)) {
            log.error(" [EFAN] dubbo client must config context-path and efan zookeeper url......");
            return;
        }
        final Method[] methods = ReflectionUtils.getUniqueDeclaredMethods(clazz);
        Arrays.stream(methods)
                .filter(e-> Objects.nonNull(e.getAnnotation(EfanClient.class)))
                .forEach(e->registered(buildJsonParams(serviceBean, e.getAnnotation(EfanClient.class), e)));
//        for (Method method : methods) {
//            EfanClient efanClient = method.getAnnotation(EfanClient.class);
//            if (Objects.nonNull(efanClient)) {
//                String contextPath = dubboConfig.getContextPath();
//                String zookeeperUrl = dubboConfig.getZookeeperUrl();
//                if (contextPath == null || "".equals(contextPath)
//                        || zookeeperUrl == null || "".equals(zookeeperUrl)) {
//                    log.error(" [EFAN] dubbo client must config context-path and efan zookeeper url......");
//                    return;
//                }
//                registered(buildJsonParams(serviceBean, efanClient, method));
//            }
//        }
    }

    private MetaDataVO buildJsonParams(final ServiceBean serviceBean, final EfanClient efanClient, final Method method) {
        String appName = dubboConfig.getAppName();
        if (appName == null || "".equals(appName)) {
            appName = serviceBean.getApplication().getName();
        }
        String path = dubboConfig.getContextPath() + efanClient.path();
        String desc = efanClient.desc();
        String serviceName = serviceBean.getInterface();
        String methodName = method.getName();
        Class<?>[] parameterTypesClazz = method.getParameterTypes();
        String parameterTypes = Arrays.stream(parameterTypesClazz).map(Class::getName)
                .collect(Collectors.joining(","));
        return MetaDataVO.builder()
                .appName(appName)
                .serviceName(serviceName)
                .methodName(methodName)
                .path(path)
                .pathDesc(desc)
                .parameterTypes(parameterTypes)
                .rpcExt(buildRpcExt(serviceBean))
                .rpcType("dubbo")
                .enabled(efanClient.enabled())
                .build();

    }

    /**
     * 获取group version 负载均衡 超时  设置
     * @param serviceBean 配有@EfanClient的类
     * @return
     */
    private String buildRpcExt(final ServiceBean serviceBean) {
        MetaDataVO.RpcExt build = MetaDataVO.RpcExt.builder()
                .group(serviceBean.getGroup())
                .version(serviceBean.getVersion())
                .loadbalance(serviceBean.getLoadbalance())
                .retries(serviceBean.getRetries())
                .timeout(serviceBean.getTimeout())
                .build();
        return JSON.toJSONString(build);

    }

    /**
     * 往zookeeper注册节点数据
     * @param metaDataVO 待注册的接口信息
     */
    private void registered(final MetaDataVO metaDataVO) {
        if(metaDataVO.isEnabled()){
            //存放节点配置信息
            zkClient.setData(String.format("/efan/metaData/%s-%s.%s", metaDataVO.getAppName(),metaDataVO.getServiceName(),metaDataVO.getMethodName())
                    ,JSON.toJSONString(metaDataVO));
        }
    }

}
