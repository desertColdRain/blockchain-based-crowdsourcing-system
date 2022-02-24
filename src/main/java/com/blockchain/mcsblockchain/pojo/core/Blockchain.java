package com.blockchain.mcsblockchain.pojo.core;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.conf.AppConfig;
import com.blockchain.mcsblockchain.enums.TransactionStatusEnum;
import com.blockchain.mcsblockchain.event.NewBlockEvent;
import com.blockchain.mcsblockchain.event.NewTransactionEvent;
import com.blockchain.mcsblockchain.net.ApplicationContextProvider;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.net.client.AppClient;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.pojo.mine.Miner;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.math.BigDecimal;
import java.util.Iterator;

@Component
public class Blockchain {
    private static Logger logger = LoggerFactory.getLogger(Blockchain.class);

    @Autowired
    private DBAccess dbAccess;

    @Autowired
    private AppClient appClient;

    @Autowired
    private Miner miner;

    @Autowired
    private TransactionPool transactionPool;
    @Autowired
    private TransactionExecutor transactionExecutor;
    @Autowired
    private AppConfig appConfig;

    // 是否正在同步区块
    private boolean syncing = true;

    /**
     * 挖取一个区块
     * @return
     */
    public Block mining() throws Exception {

        Optional<Block> lastBlock = getLastBlock();
        Block block = miner.newBlock(lastBlock);
        for (Iterator t = transactionPool.getTransactions().iterator(); t.hasNext();) {
            block.getBody().addTransaction((Transaction) t.next());
            t.remove(); // 已打包的交易移出交易池
        }
        // 存储区块
        dbAccess.putLastBlockIndex(block.getHeader().getIndex());
        dbAccess.putBlock(block);
        logger.info("Find a New Block, {}", block);

        if (appConfig.NodeDiscover()) {
            // 触发挖矿事件，并等待其他节点确认区块
            ApplicationContextProvider.publishEvent(new NewBlockEvent(block));
        } else {
            transactionExecutor.run(block);
        }
        return block;
    }

    /**
     * 发送交易
     * @param account 交易发起者的凭证
     * @param to 交易接收者
     * @param amount
     * @param data 交易附言
     * @return
     * @throws Exception
     */
    public Transaction sendTransaction(Account account, String to, BigDecimal amount, String data) throws
            Exception {

        //校验付款和收款地址
        Preconditions.checkArgument(to.startsWith("0x"), "收款地址格式不正确");
        Preconditions.checkArgument(!account.getAccountAddr().equals(to), "收款地址不能和发送地址相同");

        //构建交易对象
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setReceiverAddr(to);
        transaction.setSenderPk(account.getPublicKey());
        transaction.setReceiverPk(account.getPublicKey());
        transaction.setStatus(2);
        transaction.setContent(data);
        transaction.setTransactionHash(transaction.txHash());
        //签名
        Signature sign = Cryptography.SignAlgorithm(transaction.toString(),account.getSecretKey());
        transaction.setSenderSign(sign);

        //先验证私钥是否正确
        if (!Cryptography.SignatureVerify(transaction.toString(),account.getPublicKey(),sign)) {
            throw new RuntimeException("私钥签名验证失败，非法的私钥");
        }
        // 加入交易池，等待打包
        transactionPool.addTransaction(transaction);

        if (appConfig.NodeDiscover()) {
            //触发交易事件，向全网广播交易，并等待确认
            ApplicationContextProvider.publishEvent(new NewTransactionEvent(transaction));
        }
        return transaction;
    }

    /**
     * 获取最后一个区块
     * @return
     */
    public Optional<Block> getLastBlock() {
        return dbAccess.getLastBlock();
    }

    /**
     * 添加一个节点
     * @param ip
     * @param port
     * @return
     */
    public void addNode(String ip, int port) throws Exception {

        appClient.addNode(ip, port);
        Node node = new Node(ip, port);
        dbAccess.addNode(node);
    }
}
