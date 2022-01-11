package com.blockchain.mcsblockchain.event;

import com.blockchain.mcsblockchain.pojo.core.Transaction;
import org.springframework.context.ApplicationEvent;

//发送交易事件
public class NewTransactionEvent extends ApplicationEvent {
    //transaction：待发送的交易
    public NewTransactionEvent(Transaction transaction) {
        super(transaction);
    }
}
