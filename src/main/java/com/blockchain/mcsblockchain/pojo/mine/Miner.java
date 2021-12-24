package com.blockchain.mcsblockchain.pojo.mine;

import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.google.common.base.Optional;

public interface Miner {

    //产生一个新区快
    Block generateBlock(Optional<Block> block) throws Exception;
    //验证区块的有效性
    boolean validateBlock(Block block);
}
