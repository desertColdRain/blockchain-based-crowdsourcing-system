package com.blockchain.mcsblockchain.event;

import org.springframework.context.ApplicationEvent;

//增加区块确认数事件
public class BlockConfirmNumEvent extends ApplicationEvent {

    //  blockIndex：区块高度
    public BlockConfirmNumEvent(Integer blockIndex){
        super(blockIndex);

    }
}
