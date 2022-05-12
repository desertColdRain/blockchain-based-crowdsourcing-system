package com.blockchain.mcsblockchain.controller.node;

import com.blockchain.mcsblockchain.common.dto.NodeDto;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.service.inter.node.NodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class NodeController {
    @Autowired
    NodeService nodeService;

    //获取当前节点列表
    @CrossOrigin
    @GetMapping("node/view")
    public Result getNodeList(){
        return nodeService.getNodeList();
    }

    @CrossOrigin
    @PostMapping("node/add")
    public Result addNodeTo(@RequestBody NodeDto nodeDto) throws Exception {
        return nodeService.addNodeTo(nodeDto);
    }

    @CrossOrigin
    @PostMapping("node/delete")
    public Result deleteNode(@RequestBody Node node){
        return nodeService.deleteNode(node);
    }
}
