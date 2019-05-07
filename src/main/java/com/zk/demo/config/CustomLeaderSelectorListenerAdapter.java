package com.zk.demo.config;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.CountDownLatch;

public class CustomLeaderSelectorListenerAdapter extends
        LeaderSelectorListenerAdapter implements DisposableBean {
    @Autowired
    private Globals globals;
    private LeaderSelector leaderSelector;
    public CountDownLatch countDownLatch = new CountDownLatch(1);

    public CustomLeaderSelectorListenerAdapter(CuratorFramework client, String path) {
        this.leaderSelector = new LeaderSelector(client, path, this);
        /**
         * 自动重新排队
         * 该方法的调用可以确保此实例在释放领导权后还可能获得领导权
         */
        leaderSelector.autoRequeue();
    }

    public void start() {
        leaderSelector.start();
    }

    /**
     * 每个实例会创建一个临时有序节点，序号最小的获得master锁，即获取领导权
     * takeLeadership方法执行完成后会释放锁，如果要保持一直持有锁，则阻塞当前方法，比如用countDownLatch，在Bean销毁后释放锁，即让此方法执行完毕,
     * 之后监听这个节点的另一个实例(序号是除当前放弃领导权的节点之外最小的)会收到通知，从而获取锁，以此类推，
     * 不需要通知所有实例，按照序号顺序通知，减少服务器压力，避免羊群效应
     */
    @Override
    public void takeLeadership(CuratorFramework client) throws Exception {
        String name = globals.getAppName();
        System.out.println(name + "成为当前leader");

        //TODO 其他业务代码
        try {
//            Thread.sleep(5000);
            countDownLatch.await();
        } catch (InterruptedException e) {
            System.err.println(name + "已被中断");
            Thread.currentThread().interrupt();
        } finally {
            System.out.println(name + "放弃领导权\n");
        }

    }

    @Override
    public void destroy() throws Exception {
        //bean销毁时放弃领导权
        countDownLatch.countDown();
    }
}
