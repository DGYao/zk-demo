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
import java.util.Random;
import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class WriteServiceTest {
    @Autowired
    private WriteService createGroup;
    private Random random = new Random();

    /**
     * init
     * @throws InterruptedException
     * @throws KeeperException
     * @throws IOException
     */
    @Before
    public void init() throws KeeperException, InterruptedException, IOException {
        createGroup.connect(ZKParam.hosts);
    }

    @Test
    public void testCreateGroup() throws KeeperException, InterruptedException {
        createGroup.create(ZKParam.groupName);
    }

    @Test
    public void write() throws KeeperException, InterruptedException {
        while (true) {
            String value = random.nextInt(100) + "";
            System.out.printf("Set %s to %s\n", ZKParam.path, value);
            createGroup.write(ZKParam.path, value);
            TimeUnit.SECONDS.sleep(random.nextInt(100));
        }
    }

    /**
     * 销毁资源
     */
    @After
    public void destroy() {
        try {
            createGroup.close();
            System.gc();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}