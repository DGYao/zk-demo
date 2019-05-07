package com.zk.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class TdInitializer {

    private static final Logger logger = LoggerFactory.getLogger(TdInitializer.class);

    private AtomicBoolean initialized = new AtomicBoolean(false);

    public void init() {
        if(initialized.compareAndSet(false, true)) {
            logger.info("{} 类开始执行初始化方法", this.getClass().getSimpleName());
            try {
                doInit();

            } catch (Exception e) {

                if(isFatal()) {
                    throw e;
                } else {
                    logger.error("", e);
                }
            } finally {

                logger.info("{} 类初始化方法执行完成", this.getClass().getSimpleName());

            }
        }

    }

    protected boolean isFatal() {
        return false;
    }

    protected abstract void doInit();



}
