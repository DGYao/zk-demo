package com.zk.demo.service;

import com.zk.demo.constant.ZKParam;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import static com.zk.demo.constant.ZKParam.SESSION_TIMEOUT;

@Component
public class WriteService implements Watcher{

    private ZooKeeper zk = null;
    private CountDownLatch countDownLatch = new CountDownLatch(1);//同步计数器

    public void process(WatchedEvent event) {
        if(event.getState() == Watcher.Event.KeeperState.SyncConnected){
            countDownLatch.countDown();//计数器减一
        }
    }

    /**
     * 创建zk对象
     * 当客户端连接上zookeeper时会执行process(event)里的countDownLatch.countDown()，计数器的值变为0，则countDownLatch.await()方法返回。
     * @param hosts
     * @throws IOException
     * @throws InterruptedException
     */
    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
        countDownLatch.await();//阻塞程序继续执行
    }

    /**
     * 创建group
     *
     * @param groupName 组名
     * @throws KeeperException
     * @throws InterruptedException
     */
    public void create(String groupName) throws KeeperException, InterruptedException {
        String path = "/" + groupName;
        String createPath = zk.create(path, null, ZooDefs.Ids.OPEN_ACL_UNSAFE/*允许任何客户端对该znode进行读写*/, CreateMode.PERSISTENT/*持久化的znode*/);
        System.out.println("Created " + createPath);
    }

    public void write(String path,String value) throws KeeperException, InterruptedException {
        Stat stat = zk.exists(path, false);
        if(stat==null){
            zk.create(path, value.getBytes(ZKParam.CHARSET), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }else{
            zk.setData(path, value.getBytes(ZKParam.CHARSET),-1);
        }
    }
    /**
     * 关闭zk
     * @throws InterruptedException
     */
    public void close() throws InterruptedException {
        if(zk != null){
            try {
                zk.close();
            } catch (InterruptedException e) {
                throw e;
            }finally{
                zk = null;
                System.gc();
            }
        }
    }
}
