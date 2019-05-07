package com.zk.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LeaderSelectInit extends TdInitializer {
    @Autowired
    private CustomLeaderSelectorListenerAdapter customLeaderSelectorListenerAdapter;
    @Override
    protected void doInit() {
        try {
            customLeaderSelectorListenerAdapter.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
