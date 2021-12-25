package com.blockchain.mcsblockchain.pojo.mine.RPCA;

import com.blockchain.mcsblockchain.Utils.Constants;
import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.*;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.pojo.net.base.Node;
import com.google.common.base.Optional;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Objects;

public class RPCAConsensusImpl implements RPCAConsensus{

    private Account account;
    private DBAccess dbAccess;
    @Override
    public TxCandidateSet genTxCandidate() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        TxCandidateSet txCandidate=new TxCandidateSet();
        TransactionPool txPool=dbAccess.getAllTxs();
        for(Transaction tx:txPool.getTransactions()){
            String msg=    "senderPk=" + tx.getSenderPk() +
                    ", receiverPk=" + tx.getReceiverPk() +
                    ", transactionType=" + tx.getTransactionType() +
                    ", content='" + tx.getContent() + '\'' +
                    ", receiverAddr='" + tx.getReceiverAddr() + '\'' +
                    ", amount=" + tx.getAmount() +
                    ", blockNum=" + tx.getBlockNum() +
                    '}';
            //交易验证成功，加入本机的交易候选集
            if(Objects.equals(tx.getTransactionHash(), tx.txHash())
                    &&Cryptography.SignatureVerify(msg,tx.getSenderPk(),tx.getSenderSign())){
                txCandidate.addTransaction(tx);
            }
            else{       //如果签名和哈希验证不成功，则将此交易从Rocks DB中删除
                dbAccess.deleteTransaction(tx.getTransactionHash());
            }
        }
        return txCandidate;
    }

    @Override
    public void unionTxCandidate(List<Node> nodes,TxCandidateSet txCandidateSet) {
        for(Node node:nodes){
            TxCandidateSet nodeTxSet=node.getTxCandidateSet();
            txCandidateSet.getCandidateSet().removeAll(nodeTxSet.getCandidateSet());
            txCandidateSet.getCandidateSet().addAll(nodeTxSet.getCandidateSet());
        }
    }


   @Override
    public Block generateBlock(Optional<Block> block) throws Exception {
        Block newBlock = new Block();
        Account account=new Account();
        Optional<Account> minerAccount = dbAccess.getMinerAccount();
        if (!minerAccount.isPresent()) {            //没有挖矿账户
            throw new RuntimeException("没有找到挖矿账户，请先创建挖矿账户.");
        }
        if(!block.isPresent()){             //创建创世区块
            newBlock=createGenesisBlock();
        }
        else{
            //初始化区块头相关信息
            BlockHeader blockHeader = new BlockHeader(block.get().getHeader().getIndex()+1,
                    block.get().getHeader().getPreHash(),minerAccount.get().getAccountAddr());
            blockHeader.setHash(blockHeader.headerHash());
            BlockBody blockBody = new BlockBody();

            newBlock.setHeader(blockHeader);
            newBlock.setBody(blockBody);
        }

        Transaction transaction = new Transaction();
        transaction.setContent("Miner rewards");
        transaction.setTransactionHash(transaction.txHash());
        transaction.setAmount(Constants.mineBonus);
        transaction.setReceiverAddr(account.getAccountAddr());

        if(block.isPresent()){          //不是创世区块，则进行RPCA共识
            /*
            广播得出的区块的哈希，收到区块哈希之后，  结合自己生成的区块哈希，对每个区块哈希计算一个比例，
             某个哈希值比例超过阈值，则认为这个区块是共识通过的哈希
             broadcast(generateBlock().getHeader().getHash())
             if(rate(hash)>80%)
             {
             dbAccess.putLastBlockIndex(block.get().getHeader().getIndex());
            dbAccess.putBlock(block.get());
           }
           共识成功
             */


        }
        dbAccess.putLastBlockIndex(newBlock.getHeader().getIndex());
        return newBlock;
    }


    //创世区块的创建函数
    private Block createGenesisBlock() throws NoSuchAlgorithmException {
        Block block = new Block();
        BlockBody blockBody = new BlockBody();
        BlockHeader blockHeader = new BlockHeader();
        blockHeader.setHash(blockHeader.headerHash());
        block.setBody(blockBody);
        block.setHeader(blockHeader);
        return block;
    }

    public boolean validateBlock(Block block) {
        return false;
    }
}
