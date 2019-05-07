package com.zk.demo.service;

import com.zk.demo.constant.ZKParam;
import org.apache.zookeeper.KeeperException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ReadServiceTest {
    @Autowired
    private ReadService readService;
    @Before
    public void init() throws KeeperException, InterruptedException, IOException {
        readService.connect(ZKParam.hosts);
    }

    @Test
    public void read() throws KeeperException, InterruptedException {
        readService.read(ZKParam.path);
        Thread.sleep(Long.MAX_VALUE);
    }

    @After
    public void destroy() {
        try {
            readService.close();
            System.gc();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
