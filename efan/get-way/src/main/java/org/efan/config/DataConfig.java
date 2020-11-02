package org.efan.config;

import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.efan.common.vo.MetaData;

import java.util.concurrent.ConcurrentMap;

/**
 * @author liuf
 * @date 2020/3/5 16:44
 */
public class DataConfig {
    /**
     * dubbo监听缓存
     */
    public static final ConcurrentMap<String, MetaData> META_DATA = Maps.newConcurrentMap();

}
