package com.blockchain.mcsblockchain.Utils;

import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.crypto.PKType;

import java.math.BigDecimal;

public class Constants {

    public static final BigDecimal mineBonus=new BigDecimal("5.0");             //挖矿奖励
    public static final Account genesisAccount=null;                       //创世账户
    //区块共识需要达到的阈值
    public static final double blockConsensusThreshold=0.8;
    //每个账号初始信任值
    public static final double TRUST_VALUE=0.001;
    //返回请求，客户端问题
    public static final int CODE_FAIL_CLIENT = 400;
     //返回请求，服务端问题
     public static final int CODE_FAIL_SERVER = 500;
     //请求正确返回
    public static final int CODE_SUCCESS = 200;
}
