package com.blockchain.mcsblockchain.service.inter.node;

import com.blockchain.mcsblockchain.common.dto.NodeDto;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.pojo.Return.Result;



public interface NodeService {
    Result getNodeList();
    Result addNodeTo(NodeDto nodeDto) throws Exception;
    Result deleteNode(Node node);
}
