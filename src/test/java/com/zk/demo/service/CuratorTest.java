package com.zk.demo.service;

import com.zk.demo.config.CuratorInit;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.transaction.CuratorOp;
import org.apache.curator.framework.api.transaction.CuratorTransactionResult;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CuratorTest {
    @Autowired
    private CuratorInit curatorInit;

    private CuratorFramework client = null;

    //    RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
//    @Before
//    public void connect(){
//        client = CuratorFrameworkFactory.newClient(ZKParam.hosts, ZKParam.SESSION_TIMEOUT, ZKParam.CONNECTION_TIMEOUT, retryPolicy);
//        //启动
//        client.start();
//    }
    @Before
    public void connect() {
        client = curatorInit.getClient();
    }
//
//    @After
//    public void destory(){
//        client.close();
//    }

    @Test
    public void createNode() throws Exception {
        //创建永久节点
        Stat stat = client.checkExists().forPath("/curator");
        if (stat == null) {
            client.create().forPath("/curator", "/curator data".getBytes());
        }

        //创建永久有序节点
        client.create().withMode(CreateMode.PERSISTENT_SEQUENTIAL).forPath("/curator_sequential", "/curator_sequential data".getBytes());

        //创建临时节点
        client.create().withMode(CreateMode.EPHEMERAL)
                .forPath("/curator/ephemeral", "/curator/ephemeral data".getBytes());

        //创建临时有序节点
        client.create().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/curator/ephemeral_path1", "/curator/ephemeral_path1 data".getBytes());

        client.create().withProtection().withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                .forPath("/curator/ephemeral_path2", "/curator/ephemeral_path2 data".getBytes());

        //异步设置某个节点数据
        client.setData().inBackground().forPath("/curator", "/curator modified data with Async".getBytes());
        //获取数据
        client.getData().inBackground().forPath("/curator");

    }

    @Test
    public void testTransaction() throws Exception {
        //定义几个基本操作
        CuratorOp createOp = client.transactionOp().create()
                .forPath("/curator/one_path", "some data".getBytes());

        CuratorOp setDataOp = client.transactionOp().setData()
                .forPath("/curator", "other data".getBytes());

        CuratorOp deleteOp = client.transactionOp().delete()
                .forPath("/curator/one_path");

        //事务执行结果
        List<CuratorTransactionResult> results = client.transaction()
                .forOperations(createOp, setDataOp, deleteOp);

        //遍历输出结果
        for (CuratorTransactionResult result : results) {
            System.out.println("执行结果是： " + result.getForPath() + "--" + result.getType());
        }
    }

    @Test
    public void testNamespace() throws Exception {
        //创建带命名空间的连接实例
        client.create().orSetData().creatingParentContainersIfNeeded()
                .forPath("/server1", "server data".getBytes());
    }

    @Test
    public void testListener() throws Exception {
//        client.create().orSetData().creatingParentContainersIfNeeded().forPath("/a","a".getBytes());
        client.create().orSetData().creatingParentContainersIfNeeded().forPath("/a/b/c/d/f","abcdf".getBytes());
        client.delete().deletingChildrenIfNeeded().forPath("/a/b/c");
        System.out.println(client.getData().inBackground().forPath("/a/b"));
//        client.delete().forPath("/a/b");
    }

    @Test
    public void delete(){
        try {
            client.delete().deletingChildrenIfNeeded().forPath("/yao");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
