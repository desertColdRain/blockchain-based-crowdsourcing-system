package com.blockchain.mcsblockchain.pojo.mine.PoW;

import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.core.BlockBody;
import com.blockchain.mcsblockchain.pojo.core.BlockHeader;
import com.blockchain.mcsblockchain.pojo.core.Transaction;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.pojo.mine.Miner;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;

@Component
public class PowMiner implements Miner {
    @Autowired
    private DBAccess dbAccess;

    @Override
    public Block newBlock(Optional<Block> block) throws NoSuchAlgorithmException {

        //获取挖矿账户
        Account account;
        Optional<Account> minerAccount = dbAccess.getMinerAccount();
        if (!minerAccount.isPresent()) {
            throw new RuntimeException("没有找到挖矿账户，请先创建挖矿账户.");
        }
        Block newBlock;
        if (block.isPresent()) {
            Block prev = block.get();
            BlockHeader header = new BlockHeader(prev.getHeader().getIndex()+1, prev.getHeader().getHash());
            BlockBody body = new BlockBody();
            newBlock = new Block(body, header);
        } else {
            //创建创世区块
            newBlock = createGenesisBlock();
        }
        //创建挖矿奖励交易
        Transaction transaction = new Transaction();

        account = minerAccount.get();
        transaction.setReceiverAddr(account.getAccountAddr());
        transaction.setContent("Miner Reward.");
        transaction.setTransactionHash(transaction.txHash());
        transaction.setAmount(Miner.MINING_REWARD);

        //如果不是创世区块，则使用工作量证明挖矿
        if (block.isPresent()) {
            ProofOfWork proofOfWork = ProofOfWork.newProofOfWork(newBlock);
            PowResult result = proofOfWork.run();
            newBlock.getHeader().setDifficulty(result.getTarget());
            newBlock.getHeader().setNonce(result.getNonce());
            newBlock.getHeader().setHash(result.getHash());
        }
        newBlock.getBody().addTransaction(transaction);

        //更新最后一个区块索引
        dbAccess.putLastBlockIndex(newBlock.getHeader().getIndex());
        return newBlock;
    }

    /**
     * 创建创世区块
     * @return
     */
    private Block createGenesisBlock() throws NoSuchAlgorithmException {

        BlockHeader header = new BlockHeader(1, null);
        header.setNonce(PowMiner.GENESIS_BLOCK_NONCE);
        header.setDifficulty(ProofOfWork.getTarget());
        header.setHash(header.headerHash());
        BlockBody body = new BlockBody();
        return new Block(body,header);
    }

    @Override
    public boolean validateBlock(Block block) throws NoSuchAlgorithmException {
        ProofOfWork proofOfWork = ProofOfWork.newProofOfWork(block);
        return proofOfWork.validate();
    }
}
