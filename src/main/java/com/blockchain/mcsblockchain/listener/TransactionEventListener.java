package com.blockchain.mcsblockchain.listener;

import com.blockchain.mcsblockchain.Utils.SerializeUtils;
import com.blockchain.mcsblockchain.event.NewTransactionEvent;
import com.blockchain.mcsblockchain.net.base.MessagePacket;
import com.blockchain.mcsblockchain.net.base.MessagePacketType;
import com.blockchain.mcsblockchain.net.client.AppClient;
import com.blockchain.mcsblockchain.pojo.core.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TransactionEventListener {

    @Autowired
    AppClient appClient;
    private static Logger logger = LoggerFactory.getLogger(TransactionEventListener.class);
    //向所有客户端广播交易

    @EventListener(NewTransactionEvent.class)
    public void sendTransaction(NewTransactionEvent event) throws IOException {
        logger.info("-------------开始广播新的交易------------");
        Transaction transaction=(Transaction) event.getSource();
        MessagePacket messagePacket = new MessagePacket();
        messagePacket.setType(MessagePacketType.REQ_CONFIRM_TRANSACTION);
        messagePacket.setBody(SerializeUtils.serialize(transaction));
        //将消息发送到群组
        appClient.sendGroup(messagePacket);

    }


}
