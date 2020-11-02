package org.efan.client.apache.dubbo.common;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author liuf
 * @date 2020/3/5 14:09
 */
@Data
@Slf4j
public class ZkClient {
    private CuratorFramework client;
    private final String zookeeperUrl;

    public ZkClient(String zookeeperUrl){
        if(StringUtils.isBlank(zookeeperUrl)){
            log.error(" [EFAN] zookeeper url is null");
        }
        this.zookeeperUrl = zookeeperUrl;
    }

    /**
     * 初始化zookeeper客户端
     */
    public void init() {
        try{
            CuratorFrameworkFactory.Builder builder   = CuratorFrameworkFactory.builder()
                    .connectString(zookeeperUrl)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .sessionTimeoutMs( 1000)
                    .connectionTimeoutMs( 1000);
            client=builder.build();
            client.start();
            String nodePath="/efan/metaData";
            if(!checkExists(nodePath)){
                client.create().creatingParentsIfNeeded().forPath(nodePath);
                log.info(" [EFAN] 创建永久Zookeeper父节点成功,nodePath:{}",nodePath);
            }
        }catch(Exception e){
            e.printStackTrace();
            log.error(" [EFAN] 初始化Zookeeper客户端失败");
        }
    }
    /**
     * 创建永久Zookeeper节点
     * @param nodePath 节点路径（如果父节点不存在则会自动创建父节点），如：/efan
     * @return java.lang.String 返回创建成功的节点路径
     */
    public String createPersistentNode(String nodePath){
        try {
            return client.create().creatingParentsIfNeeded()
                    .forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 创建永久Zookeeper节点失败,nodePath:{0}",nodePath),e);
        }
        return null;
    }

    /**
     * 创建永久Zookeeper节点
     * @param nodePath 节点路径（如果父节点不存在则会自动创建父节点），如：/efan
     * @param nodeValue 节点数据
     * @return java.lang.String 返回创建成功的节点路径
     */
    public String createPersistentNode(String nodePath, String nodeValue){
        try {
            return client.create().creatingParentsIfNeeded()
                    .forPath(nodePath,nodeValue.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 创建永久Zookeeper节点失败,nodePath:{0},nodeValue:{1}",nodePath,nodeValue),e);
        }
        return null;
    }

    /**
     * 创建临时Zookeeper节点
     * @param nodePath 节点路径（如果父节点不存在则会自动创建父节点），如：/efan
     * @param nodeValue 节点数据
     * @return java.lang.String 返回创建成功的节点路径
     */
    public String createEphemeralNode(String nodePath, String nodeValue){
        try {
            return client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(nodePath,nodeValue.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 创建临时Zookeeper节点失败,nodePath:{0},nodeValue:{1}",nodePath,nodeValue),e);
        }
        return null;
    }
    /**
     * 检查Zookeeper节点是否存在
     * @param nodePath 节点路径
     * @return boolean 如果存在则返回true
     */
    public boolean checkExists(String nodePath){
        try {
            Stat stat = client.checkExists().forPath(nodePath);
            return stat != null;
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 检查Zookeeper节点是否存在出现异常,nodePath:{0}",nodePath),e);
        }
        return false;
    }
    /**
     * 获取某个Zookeeper节点的所有子节点
     * @param nodePath 节点路径
     * @return java.util.List<java.lang.String> 返回所有子节点的节点名
     */
    public List<String> getChildren(String nodePath){
        try {
            return client.getChildren().forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 获取某个Zookeeper节点的所有子节点出现异常,nodePath:{0}",nodePath),e);
        }
        return null;
    }

    /**
     * 获取某个Zookeeper节点的数据
     * @param nodePath 节点路径
     * @return java.lang.String
     */
    public String getData(String nodePath){
        try {
            return new String(client.getData().forPath(nodePath));
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 获取某个Zookeeper节点的数据出现异常,nodePath:{0}",nodePath),e);
        }
        return null;
    }
    /**
     * 设置某个Zookeeper节点的数据
     * @param nodePath 节点路径
     */
    public void setData(String nodePath, String newNodeValue){
        try {
            //不存在则先创建
            if(!checkExists(nodePath)){
                createPersistentNode(nodePath,newNodeValue);
            }else{
                client.setData().forPath(nodePath, newNodeValue.getBytes());
            }
            log.info(MessageFormat.format(" [EFAN] 同步节点数据成功,nodePath:{0},value:{1}",
                    nodePath,newNodeValue));
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 设置某个Zookeeper节点的数据出现异常,nodePath:{0}",nodePath),e);
        }
    }

    /**
     * 删除某个Zookeeper节点
     * @param nodePath 节点路径
     */
    public void delete(String nodePath){
        try {
            client.delete().guaranteed().forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 删除某个Zookeeper节点出现异常,nodePath:{0}",nodePath),e);
        }
    }

    /**
     * 级联删除某个Zookeeper节点及其子节点
     * @param nodePath 节点路径
     */
    public void deleteChildrenIfNeeded(String nodePath){
        try {
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(nodePath);
        } catch (Exception e) {
            log.error(MessageFormat.format(" [EFAN] 级联删除某个Zookeeper节点及其子节点出现异常,nodePath:{0}",nodePath),e);
        }
    }
}
