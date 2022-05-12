package com.blockchain.mcsblockchain.controller.block;

import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.service.inter.block.BlockchainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlockchainController {

    @Autowired
    BlockchainService blockchainService;

    @CrossOrigin
    @GetMapping("block/blockchain")
    public Result getBlockchain(){
        return blockchainService.getBlockchain();
    }

    @CrossOrigin
    @GetMapping("block/header")
    public Result getBlockHeader(){
        return blockchainService.getBlockchainHead();
    }
}
