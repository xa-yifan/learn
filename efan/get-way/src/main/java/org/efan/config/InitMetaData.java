package org.efan.config;

import com.alibaba.fastjson.JSON;
import org.efan.common.client.ZkClient;
import org.efan.common.vo.MetaData;

import java.util.List;

import static org.efan.config.DataConfig.META_DATA;

/**
 * @author liuf
 * @date 2020/3/5 17:20
 */
public class InitMetaData {

    private ZkClient zkClient;
    private String url;

    public InitMetaData(ZkClient zkClient, String url) {
        this.zkClient = zkClient;
        this.url = url;
    }

    public void init() {
        ApplicationConfigCache.getInstance().init(url);
        initMeta();
    }

    /**
     * 初始化zookeeper节点信息值本地缓存
     */
    private void initMeta() {
        String nodePath = "/efan/metaData";
        List<String> childrenLists = zkClient.getChildren(nodePath);
        childrenLists.forEach(e -> {
            String data = zkClient.getData(nodePath + "/" + e);
            MetaData metaDataVO = JSON.parseObject(data, MetaData.class);
            initReferenceConfigCache(metaDataVO);
            META_DATA.put(metaDataVO.getPath(), metaDataVO);
        });
    }

    private void initReferenceConfigCache(MetaData metaDataVO) {
        ApplicationConfigCache.getInstance().build(metaDataVO);
    }
}
