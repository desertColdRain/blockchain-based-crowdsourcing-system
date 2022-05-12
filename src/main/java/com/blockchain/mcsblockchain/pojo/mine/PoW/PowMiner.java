package com.blockchain.mcsblockchain.pojo.mine.PoW;

import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.*;
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

    @Autowired
    volatile TransactionPool  transactionPool;

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
        if(transactionPool.getTransactions().size()>0){
            System.out.println("交易池大小"+transactionPool.getTransactions().size());
            if (block.isPresent()) {
                ProofOfWork proofOfWork = ProofOfWork.newProofOfWork(newBlock);
                PowResult result = proofOfWork.run();
                newBlock.getHeader().setDifficulty(result.getTarget());
                newBlock.getHeader().setNonce(result.getNonce());
                newBlock.getHeader().setHash(result.getHash());
            }
          newBlock.getBody().setTransactionList(transactionPool.getTransactions());

           /* for(int i=0;i<transactionPool.getTransactions().size();i++){
                transactionPool.removeTransaction(transactionPool.getTransactions().get(i).getTransactionHash());
            }*/
            //transactionPool.getTransactions().clear();
            //更新最后一个区块索引
        }
        else{
            Transaction transaction = new Transaction();
           // System.out.println("yoooooooooooooooooooooooooooooo");
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
        }
        dbAccess.putLastBlockIndex(newBlock.getHeader().getIndex());
        //创建挖矿奖励交易
        System.out.println("new block:"+newBlock);
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
