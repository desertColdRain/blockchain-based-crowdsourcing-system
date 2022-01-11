package com.blockchain.mcsblockchain.net.base;

public interface MessagePacketType {

    //发送字符串消息
    byte STRING_MESSAGE = 0;

    //请求生成一个新的区块（挖矿）
    byte REQ_NEW_BLOCK = 1;

    //响应生成一个新区块
    byte RES_NEW_BLOCK = -1;

    //请求确认交易
    byte REQ_CONFIRM_TRANSACTION = 2;

    //响应确认交易
    byte RES_CONFIRM_TRANSACTION = -2;

    //请求同步下一个区块
    byte REQ_SYNC_NEXT_BLOCK = 3;

    //响应同步下一个区块
    byte RES_SYNC_NEXT_BLOCK = -3;

    //请求新增区块确认数
    byte REQ_INC_CONFIRM_NUM = 4;

    //响应新增区块确认数
    byte RES_INC_CONFIRM_NUM = -4;

    //请求获取账户列表
    byte REQ_ACCOUNTS_LIST = 5;

    //响应同步账户列表
    byte RES_ACCOUNTS_LIST = -5;

    //请求获取节点列表
    byte REQ_NODE_LIST = 6;

    //响应获取节点列表
    byte RES_NODE_LIST = -6;
}
