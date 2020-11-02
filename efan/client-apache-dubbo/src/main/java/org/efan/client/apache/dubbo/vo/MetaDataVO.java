package org.efan.client.apache.dubbo.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuf
 * @date 2020/3/5 13:41
 */
@Data
@Builder
public class MetaDataVO implements Serializable {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 请求路径
     */
    private String path;
    /**
     * 描述
     */
    private String pathDesc;
    /**
     * 请求类型 目前只能是rpc 后续实现http webService等
     */
    private String rpcType;
    /**\
     * 类的路径+名称
     */
    private String serviceName;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 请求参数类型列表
     */
    private String parameterTypes;

    /**
     * dubbo 版本等信息
     */
    private String rpcExt;
    /**
     * 是否初始化
     */
    private boolean enabled;

    /**
     * The type Rpc ext.
     */
    @Data
    @Builder
    public static class RpcExt {

        private String group;

        private String version;

        private String loadbalance;

        private Integer retries;

        private Integer timeout;
    }
}
