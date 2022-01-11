package com.blockchain.mcsblockchain.conf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@EnableConfigurationProperties(AppConfig.class)
@ConfigurationProperties(prefix = "app")
public class  AppConfig {

    //是否启用节点发现
    private boolean nodeDiscover;

    //是否自动挖矿
    private boolean autoMining;

    // 最少确认数
    private int minConfirmNum = 0;

    //数据存储地址
    private String dataDir;

    public boolean NodeDiscover() {
        return nodeDiscover;
    }

    public void setNodeDiscover(boolean nodeDiscover) {
        this.nodeDiscover = nodeDiscover;
    }

    public boolean isAutoMining() {
        return autoMining;
    }

    public void setAutoMining(boolean autoMining) {
        this.autoMining = autoMining;
    }

    public String getDataDir() {
        return dataDir;
    }

    public boolean getNodeDiscover() {
        return nodeDiscover;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public int getMinConfirmNum() {
        return minConfirmNum;
    }

    public void setMinConfirmNum(int minConfirmNum) {
        this.minConfirmNum = minConfirmNum;
    }
}
