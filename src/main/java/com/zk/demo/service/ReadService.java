package com.zk.demo.service;

import com.zk.demo.constant.ZKParam;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.zk.demo.constant.ZKParam.CHARSET;
import static com.zk.demo.constant.ZKParam.SESSION_TIMEOUT;

@Service
public class ReadService implements Watcher{
    private ZooKeeper zk = null;
    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getType() == Event.EventType.NodeDataChanged){
            try{
                String value = read(ZKParam.path);
                System.out.printf("Read %s as %s\n",ZKParam.path,value);
            }catch(InterruptedException e){
                System.err.println("Interrupted. exiting. ");
                Thread.currentThread().interrupt();
            }catch(KeeperException e){
                System.out.printf("KeeperException?s. Exiting.\n", e);
            }
        }
    }

    public void connect(String hosts) throws IOException, InterruptedException {
        zk = new ZooKeeper(hosts, SESSION_TIMEOUT, this);
    }

    public String read(String path) throws KeeperException, InterruptedException{
        byte[] data = zk.getData(path, this, null);
        return new String(data,CHARSET);

    }

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
