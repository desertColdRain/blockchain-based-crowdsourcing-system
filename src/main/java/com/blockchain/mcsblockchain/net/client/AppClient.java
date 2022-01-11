package com.blockchain.mcsblockchain.net.client;

import com.blockchain.mcsblockchain.Utils.SerializeUtils;
import com.blockchain.mcsblockchain.conf.AppConfig;
import com.blockchain.mcsblockchain.event.FetchNextBlockEvent;
import com.blockchain.mcsblockchain.net.ApplicationContextProvider;
import com.blockchain.mcsblockchain.net.base.MessagePacket;
import com.blockchain.mcsblockchain.net.base.MessagePacketType;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.net.conf.TioProps;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.tio.client.AioClient;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupContext;
import org.tio.core.Aio;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;


@Component
public class AppClient {
    @Resource
    private ClientGroupContext clientGroupContext;

    @Autowired
    private TioProps tioProps;

    @Autowired
    private DBAccess dbAccess;

    private static final Logger logger = LoggerFactory.getLogger(AppClient.class);

    @Autowired
    AppConfig appConfig;

    private AioClient aioClient;

    public void test(){
        System.out.println(appConfig.NodeDiscover());
    }
    //网络客户端程序入口
    @PostConstruct
    public void clientStart() throws Exception{
        //System.out.println(appConfig.NodeDiscover());
        //判断是否启用节点发现，否的话则以单机模式运行
        if(!appConfig.NodeDiscover()) {
            return ;
        }
        aioClient = new AioClient(clientGroupContext);
        Optional<List<Node>> nodeList = dbAccess.getNodeList();
        List<Node> nodes=null;
        if(nodeList.isPresent()){
            //System.out.println("yes");
            nodes=nodeList.get();
        }
        else if (null != tioProps.getNodes()) {
            nodes = tioProps.getNodes();
        }
        if(nodes!=null){
            for (Node node : nodes) {
                connectNode(node);
                System.out.println("connect nodes");
            }
        }
        //else System.out.println("did not connect nodes");
    }

    //发送消息到群组
    public void sendGroup(MessagePacket messagePacket){
        // 关闭同步功能
        if (!appConfig.NodeDiscover()) {
            return;
        }

        Aio.sendToGroup(clientGroupContext, tioProps.getClientGroupName(), messagePacket);
    }

    //添加节点
    public void addNode(String ip,int port) throws Exception {
        if (appConfig.NodeDiscover()) {
            return;
        }
        Node node = new Node(ip, port);
        // determine if the node is already exists
        Optional<List<Node>> nodeList = dbAccess.getNodeList();
        if (nodeList.isPresent() && nodeList.get().contains(node)) {
            return;
        }
        if (dbAccess.addNode(node)) {
            connectNode(node);
        }
    }
    public void connectNode(Node node) throws Exception {
        ClientChannelContext channelContext = aioClient.connect(node);
        Aio.send(channelContext, new MessagePacket(SerializeUtils.serialize(MessagePacket.HELLO_MESSAGE)));
        Aio.bindGroup(channelContext, tioProps.getClientGroupName());
        logger.info("连接节点成功, {}", node);
    }

    //向所有的连接的节点发起同步请求
    @EventListener(ApplicationReadyEvent.class)
    public void fetchNextBlock(){
        ApplicationContextProvider.publishEvent(new FetchNextBlockEvent(0));
    }

    //开始获取节点列表
    @EventListener(ApplicationReadyEvent.class)
    public void fetchNodeList() throws IOException {
        logger.info("++++++++++++++++++++++++++ 开始获取在线节点 +++++++++++++++++++++++++++");
        MessagePacket packet = new MessagePacket();
        packet.setType(MessagePacketType.REQ_NODE_LIST);
        packet.setBody(SerializeUtils.serialize(MessagePacket.FETCH_NODE_LIST_SYMBOL));
        sendGroup(packet);
    }

}
