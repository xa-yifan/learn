package org.efan.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuf
 * @date 2020/3/5 13:41
 */
@Data
public class MetaData implements Serializable {

    private String appName;

    private String path;

    private String pathDesc;

    private String rpcType;

    private String serviceName;

    private String methodName;

    private String parameterTypes;

    private String rpcExt;

    private boolean enabled;

    private MetaData() {
    }

    /**
     * The type Rpc ext.
     */
    @Data
    public static class RpcExt {

        private String group;

        private String version;

        private String loadbalance;

        private Integer retries;

        private Integer timeout;

        private RpcExt() {
        }
    }
}
