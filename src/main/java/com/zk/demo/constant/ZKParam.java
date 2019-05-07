package com.zk.demo.constant;

import java.nio.charset.Charset;

public interface ZKParam {
    String hosts = "10.100.97.176:2181,10.100.97.176:2182,10.100.97.176:2183";
    String groupName = "zoo";
    String path = "/" + groupName + "/config";
    //会话延时
    int SESSION_TIMEOUT = 30 * 1000;
    //连接超时时间
    int CONNECTION_TIMEOUT = 3 * 1000;
    Charset CHARSET = Charset.forName("UTF-8");
}
