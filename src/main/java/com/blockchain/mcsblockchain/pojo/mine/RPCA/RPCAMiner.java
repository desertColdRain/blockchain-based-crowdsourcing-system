package com.blockchain.mcsblockchain.pojo.mine.RPCA;

import com.blockchain.mcsblockchain.Utils.Constants;
import com.blockchain.mcsblockchain.pojo.Time;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.core.BlockBody;
import com.blockchain.mcsblockchain.pojo.core.BlockHeader;
import com.blockchain.mcsblockchain.pojo.core.Transaction;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.pojo.mine.Miner;
import com.google.common.base.Optional;

import java.security.NoSuchAlgorithmException;


public class RPCAMiner implements Miner {

    private DBAccess dbAccess;
    private Account account;
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
    @Override
    public boolean validateBlock(Block block) {
        return false;
    }
}
