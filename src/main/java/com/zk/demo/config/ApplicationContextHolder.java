package com.zk.demo.config;

import org.springframework.context.ApplicationContext;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author liubin
 * @Date 2017/5/15 15:03
 */
public class ApplicationContextHolder {

    private static AtomicBoolean startup = new AtomicBoolean(false);

    public static boolean tryStartup() {
        return startup.compareAndSet(false, true);
    }

    public static boolean isStartup() {
        return startup.get();
    }

}
