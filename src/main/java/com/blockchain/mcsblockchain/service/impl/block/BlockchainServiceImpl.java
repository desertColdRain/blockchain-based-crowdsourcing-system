package com.blockchain.mcsblockchain.service.impl.block;

import com.blockchain.mcsblockchain.Utils.Constants;
import com.blockchain.mcsblockchain.Utils.JsonVo;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.service.inter.block.BlockchainService;
import com.google.common.base.Optional;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
@Service
public class BlockchainServiceImpl implements BlockchainService {

    @Autowired
    DBAccess dbAccess;
    @Override
    public Result getBlockchain() {
        System.out.println("进入区块链查询功能");
        List<Block> blocks = new ArrayList<>();
        Optional<Object> lastBlockIndex = dbAccess.getLastBlockIndex();
        if (lastBlockIndex.isPresent()) {
            int index = (int) lastBlockIndex.get();
            int n = 1;
            while (n <= index) {
                Optional<Block> block = dbAccess.getBlock(n);
                if (block.isPresent()) {
                    blocks.add(block.get());
                }
                n++;
            }
            System.out.println("block size: "+blocks.size());
            return new Result("查询区块链信息成功", Constants.CODE_SUCCESS,blocks);
        } else {

            return new Result("区块链目前还没有区块",Constants.CODE_FAIL_CLIENT,null);
        }
    }

    @Override
    public Result getBlockchainHead() {
        System.out.println("进入区块链头信息查询功能");
        Optional<Block> block = dbAccess.getLastBlock();

        if (block.isPresent()) {
            return new Result("获取区块链头信息成功", Constants.CODE_SUCCESS, block.get());
        }
        return new Result("获取区块链头信息失败，目前区块链中还没有区块", Constants.CODE_FAIL_CLIENT, null);



    }
}
