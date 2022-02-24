package com.blockchain.mcsblockchain.pojo.mine;

import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.google.common.base.Optional;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;

public interface Miner {

    BigDecimal MINING_REWARD = BigDecimal.valueOf(50);

    /**
     * 创世区块难度值
     */
    Long GENESIS_BLOCK_NONCE = 100000L;

    /**
     * 挖出一个新的区块
     * @param block
     * @return
     * @throws Exception
     */
    Block newBlock(Optional<Block> block) throws Exception;

    /**
     * 检验一个区块
     * @param block
     * @return
     */
    boolean validateBlock(Block block) throws NoSuchAlgorithmException;
}
