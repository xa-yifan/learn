package org.efan.config;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.service.GenericService;
import org.efan.common.client.ZkClient;
import org.efan.common.utils.DateUtils;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuf
 * @date 2020/3/5 18:01
 */
@Slf4j
public class Test {
    public static void main(String[] args) throws Exception {
//        ApplicationConfig applicationConfig= new ApplicationConfig("efan");
//        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
//        reference.setInterface("com.eway.pay.alipay.service.AlipayService");
//        RegistryConfig registryConfig = new RegistryConfig();
////        registryConfig.setProtocol("dubbo");
////        registryConfig.setId("efan");
////        registryConfig.setRegister(false);
//        registryConfig.setAddress("zookeeper://127.0.0.1:2181");
//        reference.setRegistry(registryConfig);
//        reference.setApplication(applicationConfig);
//        // 声明为泛化接口
//        reference.setGeneric("true");
//        GenericService genericService = reference.get();
//
//        Map<String, Object> data = new HashMap<>();
//        data.put("name", "liufan");
//        data.put("password", "yyy");
//
//        Object o=genericService.$invoke("test",new String[]
//                {"java.util.Map"}, new Object[]{data});
//        System.out.println(JSON.toJSONString(o));

//        ZkClient zkClient = new ZkClient("127.0.0.1:2181");
//        zkClient.init();
//        CuratorFramework client = zkClient.getClient();
//
//        PathChildrenCache cache =
//                new PathChildrenCache(client, "/efan/metaData", true);
//        PathChildrenCacheListener l =
//                (client1, event) -> {
//                    try {
//                        ChildData data = event.getData();
//                        switch (event.getType()) {
//                            case CHILD_ADDED:
//
//                                log.info("子节点增加, path={}, data={}",
//                                        data.getPath(), new String(data.getData(), "UTF-8"));
//
//                                break;
//                            case CHILD_UPDATED:
//                                log.info("子节点更新, path={}, data={}",
//                                        data.getPath(), new String(data.getData(), "UTF-8"));
//                                break;
//                            case CHILD_REMOVED:
//                                log.info("子节点删除, path={}, data={}",
//                                        data.getPath(), new String(data.getData(), "UTF-8"));
//                                break;
//                            default:
//                                break;
//                        }
//
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//                };
//        cache.getListenable().addListener(l);
//        cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
//        Thread.sleep(Integer.MAX_VALUE);
        String dateStr="2020-03-25 15:01:20";
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d=dateFormat.parse(dateStr);
        final LocalDateTime start = LocalDateTime.ofEpochSecond(d.getTime()/1000, 0, ZoneOffset.ofHours(8));
        final LocalDateTime now = LocalDateTime.now();
        long between = start.until(now, ChronoUnit.SECONDS);
        System.out.println(between);
    }
}
