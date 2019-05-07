package com.zk.demo.config;

import com.zk.demo.constant.ZKParam;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuratorInit{
    //创建连接实例
    private CuratorFramework client = null;

    /**
     * baseSleepTimeMs：初始的重试等待时间
     * maxRetries：最多重试次数
     */
    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

    public CuratorInit(Globals globals) {
        //创建 CuratorFrameworkImpl实例
//        client = CuratorFrameworkFactory.newClient(ZKParam.hosts, ZKParam.SESSION_TIMEOUT, ZKParam.CONNECTION_TIMEOUT, retryPolicy);
        String defaultData = "";
        try {
            defaultData = InetAddress.getLocalHost().getHostAddress()+":"+globals.getPort();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        ACLProvider aclProvider = new ACLProvider() {
            private List<ACL> acl;

            @Override
            public List<ACL> getDefaultAcl() {
                if (acl == null) {
                    ArrayList<ACL> acl = ZooDefs.Ids.CREATOR_ALL_ACL;
                    acl.clear();
                    acl.add(new ACL(ZooDefs.Perms.ALL, new Id("auth", "admin:admin")));
                    acl.add(new ACL(ZooDefs.Perms.READ, new Id("auth", "yao:yao")));
                    this.acl = acl;
                }
                return acl;
            }

            @Override
            public List<ACL> getAclForPath(String path) {
                return acl;
            }
        };

        client = CuratorFrameworkFactory.builder()
            .namespace("yao/v1")
            .connectString(ZKParam.hosts)
            .sessionTimeoutMs(ZKParam.SESSION_TIMEOUT)
            .connectionTimeoutMs(ZKParam.CONNECTION_TIMEOUT)
            .retryPolicy(retryPolicy)
            .defaultData(defaultData.getBytes())
//            .aclProvider(aclProvider)
//            .authorization("digest","admin:admin".getBytes())
            .build();
        //启动
        client.start();
/**
 * 一次性监听1
 */
//        byte[] data = new byte[0];
//        try {
//            data = client.getData().usingWatcher(new Watcher() {
//                @Override
//                public void process(WatchedEvent event) {
//                    System.out.println("获取 two 节点 监听器 : " + event);
//                }
//            }).forPath("/a/b");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        System.out.println("two 节点数据: "+ new String(data));
/**
 * 一次性监听2
 */
//        CuratorListener listener = new CuratorListener() {
//            @Override
//            public void eventReceived(CuratorFramework client, CuratorEvent event)
//                    throws Exception {
//                System.out.println("listen:"+event.getPath()+",event:"+event.toString());
//            }
//        };
//        client.getCuratorListenable().addListener(listener);
//        try {
//            client.setData().inBackground().forPath("/a/b","ab".getBytes());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

/**
 * 重复监听
 */
        //在注册监听器的时候，如果传入此参数，当事件触发时，逻辑由线程池处理
        ExecutorService pool = Executors.newFixedThreadPool(1);
//        nodeCache(pool);
//        pathChildrenCache(pool);
        treeCache(pool);

    }

    public CuratorFramework getClient() {
        return client;
    }

    private void nodeCache(ExecutorService pool) {
        //监听节点数据变化 连接 目录 是否压缩
        //节点可以进行修改操作  删除节点后会再次创建(空节点)
        final NodeCache nodeCache = new NodeCache(client, "/a/b");
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                ChildData currentData = nodeCache.getCurrentData();
                System.out.println("Node data is changed, path:" + nodeCache.getPath() + ", new data: " +
                        new String(Optional.ofNullable(currentData).isPresent() ? currentData.getData() : "".getBytes()));
            }
        }, pool);
        try {
            nodeCache.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void pathChildrenCache(ExecutorService pool) {
        //当前节点不监听，只监听子节点变化 连接  路径  是否获取数据
        //子节点之后的节点都不监听
        final PathChildrenCache pathChildrenCache = new PathChildrenCache(client, "/a", true);
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("CHILD_ADDED: " + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("CHILD_REMOVED: " + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("CHILD_UPDATED: " + event.getData().getPath());
                        break;
                    default:
                        break;
                }
            }
        }, pool);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    //监控 指定节点和节点下的所有的节点的变化--无限监听  可以进行本节点的删除(不在创建)
    private void treeCache(ExecutorService pool) {
        //设置节点的cache
        TreeCache treeCache = new TreeCache(client, "/a");
        //设置监听器和处理过程
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                ChildData data = event.getData();
                if (data != null) {
                    switch (event.getType()) {
                        case NODE_ADDED:
                            System.out.println("NODE_ADDED : " + data.getPath() + "  数据:" + new String(data.getData()));
                            break;
                        case NODE_REMOVED:
                            System.out.println("NODE_REMOVED : " + data.getPath() + "  数据:" + new String(data.getData()));
                            break;
                        case NODE_UPDATED:
                            System.out.println("NODE_UPDATED : " + data.getPath() + "  数据:" + new String(data.getData()));
                            break;

                        default:
                            break;
                    }
                } else {
                    System.out.println("data is null : " + event.getType());
                }
            }
        }, pool);
        //开始监听
        try {
            treeCache.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
