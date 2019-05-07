package com.zk.demo.config;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import static com.zk.demo.constant.ZKParam.hosts;
//@Component
public class ZKClientInit extends TdInitializer {
    private ZkClient zk;
    private String nodeName = "/zkClientTest";
    private String subNodeName = nodeName+"/a";
    @Override
    protected void doInit() {
        zk = new ZkClient(hosts);
        zk.subscribeDataChanges(nodeName, new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("node data changed!");
                System.out.println("node=>" + s);
                System.out.println("data=>" + o);
                System.out.println("--------------");
            }

            public void handleDataDeleted(String s) throws Exception {
                System.out.println("node data deleted!");
                System.out.println("s=>" + s);
                System.out.println("--------------");

            }
        });

    }
}
