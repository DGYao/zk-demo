package com.zk.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitBean {
    @Bean
    public CuratorInit curatorInit(Globals globals){
        return new CuratorInit(globals);
    }

    @Bean
    public CustomLeaderSelectorListenerAdapter customLeaderSelectorListenerAdapter(CuratorInit curatorInit,Globals globals){
        return new CustomLeaderSelectorListenerAdapter(curatorInit.getClient(),"/"+globals.getAppName()+"/"+globals.getActive());
    }
}
