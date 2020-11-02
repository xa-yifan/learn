package org.efan.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * request请求类
 *
 * @author 小帆
 */
@Data
public class RequestVO implements Serializable {

    /**
     * 应用名称
     */
    private String module;

    /**
     * is method name .
     */
    private String method;

    /**
     * this is sign .
     */
    private String sign;

    /**
     * appKey .
     */
    private String appId;

    /**
     * path.
     */
    private String path;

    /**
     * the metaData.
     */
    private MetaData metaData;
    /**
     * startDateTime.
     */
    private LocalDateTime startDateTime;

}
