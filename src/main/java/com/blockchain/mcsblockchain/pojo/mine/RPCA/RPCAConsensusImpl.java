package com.blockchain.mcsblockchain.pojo.mine.RPCA;

import com.blockchain.mcsblockchain.Utils.Constants;
import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.*;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.google.common.base.Optional;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
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
    public void unionTxCandidate(TxCandidateSet txCandidateSet) {
        Optional<List<Node>> nodeList = dbAccess.getNodeList();
        if(nodeList.isPresent()){
            if(nodeList.get().size()==0) return ;
            for(Node node:nodeList.get()){
                TxCandidateSet nodeTxSet=node.getTxCandidateSet();
                txCandidateSet.getCandidateSet().removeAll(nodeTxSet.getCandidateSet());
                txCandidateSet.getCandidateSet().addAll(nodeTxSet.getCandidateSet());
            }
        }

    }

   @Override
    public void generateBlock() throws Exception {
        Block newBlock = new Block();
        Account account=new Account();
        Optional<Account> minerAccount = dbAccess.getMinerAccount();
       Optional<Block> block = dbAccess.getLastBlock();
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
            TxCandidateSet txCandidateSet = genTxCandidate();
            unionTxCandidate(txCandidateSet);
            blockBody.setTransactionList(txCandidateSet.getCandidateSet());
            newBlock.setHeader(blockHeader);
            newBlock.setBody(blockBody);
        }

        Transaction transaction = new Transaction();
        transaction.setContent("Miner rewards");
        transaction.setTransactionHash(transaction.txHash());
        transaction.setAmount(Constants.mineBonus);
        transaction.setReceiverAddr(account.getAccountAddr());


        if(block.isPresent()){          //不是创世区块，则进行RPCA共识
            Optional<List<Node>> nodeList = dbAccess.getNodeList();
            if(nodeList.isPresent()){
                if(nodeList.get().size()==0) {      //单机模式，直接将自己生成的区块加入到区块链中
                    dbAccess.putLastBlockIndex(newBlock.getHeader().getIndex());
                    dbAccess.putBlock(newBlock);
                }
                else{   //获取节点生成的区块，并为每一个区块计算一个比例，比例超过阈值的时候便共识成功
                    StringBuilder finalHash=new StringBuilder();            //最终比例最高的区块的哈希
                    List<String> consensusBlockHash = new ArrayList<>();    //存储区块哈希的列表
                    consensusBlockHash.add(newBlock.getHeader().getHash()); //加入本机产生的新区块的哈希
                    for (Node node:nodeList.get()){                         //将UNL中节点产生的区块的哈希加入到列表
                       consensusBlockHash.add(node.getConsensusBlock().getHeader().getHash());
                    }
                    Collections.sort(consensusBlockHash);                   //对列表进行排序
                    int max=0;                                              //区块哈希值相同的数的最大值
                    int count=1;
                    for(int i=0;i<consensusBlockHash.size()-1;i++){
                        if(consensusBlockHash.get(i).equals(consensusBlockHash.get(i+1))){
                            count++;
                        }
                        else{
                            if(count>max){
                                finalHash.deleteCharAt(0);
                                finalHash.append(consensusBlockHash.get(i));
                                max=count;
                            }
                            count=1;
                        }
                    }
                    double maxRate=max/consensusBlockHash.size();       //比例最高的区块哈希值在所有区块中占比
                    if(maxRate>=Constants.blockConsensusThreshold){     //高于阈值，将此区块作为新产生的区块
                        System.out.println("共识成功，新产生的区块的hash是"+finalHash.toString());
                    }
                    for (Node node:nodeList.get()){                     //利用区块哈希值找出相应的区块
                        if(Objects.equals(node.getConsensusBlock().getHeader().headerHash(), finalHash.toString())){
                            dbAccess.putBlock(node.getConsensusBlock());        //区块上链
                            dbAccess.putLastBlockIndex(node.getConsensusBlock().getHeader().getIndex());
                        }
                    }
                }
            }
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
