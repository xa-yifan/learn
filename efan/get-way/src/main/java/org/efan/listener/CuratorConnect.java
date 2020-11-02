package org.efan.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.efan.common.client.ZkClient;
import org.efan.common.vo.MetaData;
import org.efan.config.ApplicationConfigCache;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static org.efan.config.DataConfig.META_DATA;

/**
 * 节点监听
 * @author liuf
 * @date 2020/3/5 23:10
 */
@Component
@Slf4j
public class CuratorConnect  implements ApplicationRunner {
    private static final String NODE_PATH="/efan/metaData";
    @Resource
    private ZkClient zkClient;


    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info(" [EFAN] 开始监听节点");
        CuratorFramework client=zkClient.getClient();
        final PathChildrenCache childrenCache = new PathChildrenCache(client, NODE_PATH, true);

        /**
         * StartMode: 初始化方式
         * POST_INITIALIZED_EVENT：异步初始化，初始化之后会触发事件
         * NORMAL：异步初始化
         * BUILD_INITIAL_CACHE：同步初始化
         */
        childrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);

        // 添加事件监听器
        childrenCache.getListenable().addListener((curatorFramework, event) -> {
            // 通过判断event type的方式来实现不同事件的触发
            if (event.getType().equals(PathChildrenCacheEvent.Type.INITIALIZED)) {
                // 子节点初始化时触发
                log.info(" [EFAN] 子节点初始化成功");
            } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_ADDED)) { // 添加子节点时触发
                String data=new String(event.getData().getData());
                log.info(" [EFAN] 子节点新增成功 path:{},value:{}",event.getData().getPath(),data);
                MetaData metaDataVO = JSON.parseObject(data, MetaData.class);
                //设置metaData缓存
                META_DATA.put(metaDataVO.getPath(), metaDataVO);
                //泛化缓存
                ApplicationConfigCache.getInstance().build(metaDataVO);
            } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {  // 删除子节点时触发
//                MetaData metaDataVO = JSON.parseObject(data, MetaData.class);
//                META_DATA.remove(metaDataVO.getPath());
//                ApplicationConfigCache.getInstance().build(metaDataVO);
//                ApplicationConfigCache.getInstance().invalidate(metaDataVO.getServiceName());
            } else if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_UPDATED)) {  // 修改子节点数据时触发
                String data=new String(event.getData().getData());
                log.info(" [EFAN] 子节点更新成功 path:{},value:{}",event.getData().getPath(),data);
                MetaData metaDataVO = JSON.parseObject(data, MetaData.class);
                META_DATA.remove(metaDataVO.getPath());
                ApplicationConfigCache.getInstance().invalidate(metaDataVO.getServiceName());
                META_DATA.put(metaDataVO.getPath(), metaDataVO);
                ApplicationConfigCache.getInstance().build(metaDataVO);
            }
        });
    }
}
