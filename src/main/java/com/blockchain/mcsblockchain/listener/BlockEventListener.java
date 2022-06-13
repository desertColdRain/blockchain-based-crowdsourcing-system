package com.blockchain.mcsblockchain.listener;

import com.blockchain.mcsblockchain.Utils.SerializeUtils;
import com.blockchain.mcsblockchain.event.BlockConfirmNumEvent;
import com.blockchain.mcsblockchain.event.FetchNextBlockEvent;
import com.blockchain.mcsblockchain.event.NewBlockEvent;
import com.blockchain.mcsblockchain.net.base.MessagePacket;
import com.blockchain.mcsblockchain.net.base.MessagePacketType;
import com.blockchain.mcsblockchain.net.client.AppClient;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

//区块事件监听器
@Component
public class BlockEventListener {

    @Autowired
    private DBAccess dbAccess;

    @Autowired
    AppClient appClient;

    private static Logger logger = LoggerFactory.getLogger(BlockEventListener.class);

    //新区块产生事件
    @EventListener(NewBlockEvent.class)
    public void newBlock(NewBlockEvent event) throws IOException {
        logger.info("++++++++++++++++++开始广播新区块+++++++++++++++++++");
        //source 为事件最初发生的对象身上
        Block block = (Block) event.getSource();
        MessagePacket messagePacket = new MessagePacket();
        messagePacket.setType(MessagePacketType.REQ_NEW_BLOCK);
        messagePacket.setBody(SerializeUtils.serialize(block));
        //将消息发送到群组
        appClient.sendGroup(messagePacket);
    }

    //同步下一个区块
    @EventListener(FetchNextBlockEvent.class)
    public void fetchNextBlock(FetchNextBlockEvent event) throws IOException {
        logger.info("+++++++++++++++++开始同步下一个区块++++++++++++++++++++++");
        Integer blockIndex = (Integer) event.getSource();
        if(blockIndex==0){
            Optional<Object> lastBlockIndex = dbAccess.getLastBlockIndex();
            if(lastBlockIndex.isPresent()){
                blockIndex=(Integer) lastBlockIndex.get();
            }
        }
        MessagePacket messagePacket = new MessagePacket();
        messagePacket.setType(MessagePacketType.REQ_SYNC_NEXT_BLOCK);
        messagePacket.setBody(SerializeUtils.serialize(blockIndex+1));
        //将消息发送到群组
        appClient.sendGroup(messagePacket);
    }

    @EventListener(BlockConfirmNumEvent.class)
    public void IncBlockConfirmNum(BlockConfirmNumEvent event) throws IOException {
        logger.info("------------增加区块确认数------------");
        Integer blockIndex = (Integer) event.getSource();
        MessagePacket messagePacket = new MessagePacket();
        messagePacket.setType(MessagePacketType.RES_INC_CONFIRM_NUM);
        messagePacket.setBody(SerializeUtils.serialize(blockIndex));
        //将消息发送到群组
        appClient.sendGroup(messagePacket);
    }

}
