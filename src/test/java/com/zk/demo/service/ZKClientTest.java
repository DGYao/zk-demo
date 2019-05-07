package com.zk.demo.service;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.zk.demo.constant.ZKParam.hosts;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ZKClientTest {

    private ZkClient zk;
//
    private String nodeName = "/yao";
    private String subNodeName = nodeName+"/a";
//
    @Before
    public void initTest() {
        zk = new ZkClient(hosts);
        zk.addAuthInfo("digest","yao:yao".getBytes());
    }

    @After
    public void dispose() {
        zk.close();
    }
//
//    @Test
//    public void testListener() throws InterruptedException {
//        // 监听指定节点的数据变化
//
//        zk.subscribeDataChanges(nodeName, new IZkDataListener() {
//            public void handleDataChange(String s, Object o) throws Exception {
//                System.out.println("node data changed!");
//                System.out.println("node=>" + s);
//                System.out.println("data=>" + o);
//                System.out.println("--------------");
//            }
//
//            public void handleDataDeleted(String s) throws Exception {
//                System.out.println("node data deleted!");
//                System.out.println("s=>" + s);
//                System.out.println("--------------");
//
//            }
//        });
//
//        System.out.println("ready!");
//
//        // junit测试时，防止线程退出
//        while (true) {
//            TimeUnit.SECONDS.sleep(5);
//        }
//    }

    @Test
    public void testUpdateConfig() throws InterruptedException {
        if (!zk.exists(nodeName)) {
            zk.createPersistent(nodeName);
        }
        zk.writeData(nodeName, "2".getBytes());
//        zk.writeData(nodeName, "2");
//        zk.delete(nodeName);
//        zk.delete(nodeName);
//        zk.writeData(subNodeName, "a");
    }
}
