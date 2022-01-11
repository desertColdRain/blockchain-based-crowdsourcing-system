package com.blockchain.mcsblockchain.event;

import org.springframework.context.ApplicationEvent;

//当发起同步下一个区块请求的时候会触发该事件
public class FetchNextBlockEvent extends ApplicationEvent {

    //blockIndex：区块高度
    public FetchNextBlockEvent(Integer blockIndex) {
        super(blockIndex);
    }
}
