package com.blockchain.mcsblockchain.event;

import com.blockchain.mcsblockchain.pojo.core.Block;
import org.springframework.context.ApplicationEvent;

//当有一个新的区块产生将会触发该事件
public class NewBlockEvent extends ApplicationEvent {

    public NewBlockEvent(Block block) {
        super(block);
    }
}
