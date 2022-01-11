package com.blockchain.mcsblockchain.pojo.core;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.google.common.base.Optional;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Iterator;

@Component
public class TransactionExecutor {

    private DBAccess dbAccess;

    private TransactionPool transactionPool;

    /**
     * 执行区块中的交易

     */
    public void run(Block block) throws Exception {

        for (Transaction transaction : block.getBody().getTransactionList())
        {
            Optional<Account> recipient = dbAccess.getAccount(transaction.getReceiverAddr());
            //如果收款地址账户不存在，则创建一个新账户
            /*if (!recipient.isPresent()) {
                recipient = Optional.of(new Account(transaction.getTo(), BigDecimal.ZERO));
            }*/
            //挖矿奖励
            if (null == transaction.getSenderPk()) {
                recipient.get().setBalance(recipient.get().getBalance().add(transaction.getAmount()));
                dbAccess.putAccount(recipient.get());
                continue;
            }
            //账户转账
            Optional<Account> sender = dbAccess.getAccount(Cryptography.myHash(transaction.getSenderPk().value.toString()));
            //验证签名
            String msg=    "senderPk=" + transaction.getSenderPk() +
                    ", receiverPk=" + transaction.getReceiverPk()+
                    ", transactionType=" + transaction.getTransactionType() +
                    ", content='" + transaction.getContent() + '\'' +
                    ", receiverAddr='" + transaction.getReceiverAddr() + '\'' +
                    ", amount=" + transaction.getAmount() +
                    ", blockNum=" + transaction.getBlockNum() +
                    '}';
            boolean verify = Cryptography.SignatureVerify(
                    msg,
                    transaction.getSenderPk(),
                    transaction.getSenderSign());
            if (!verify) {
                /*transaction.setStatus(TransactionStatusEnum.FAIL);
                transaction.setErrorMessage("交易签名错误");*/
                System.out.println("交易签名错误");
                continue;
            }
            //验证账户余额
            if (sender.get().getBalance().compareTo(transaction.getAmount()) == -1) {
               /* transaction.setStatus(TransactionStatusEnum.FAIL);
                transaction.setErrorMessage("账户余额不足");*/
                System.out.println("账户余额不足");
                continue;
            }

            // 更新交易区块高度
            transaction.setBlockNum(block.getHeader().getIndex());
            // 缓存交易哈希对应的区块高度, 方便根据 hash 查询交易状态
            dbAccess.put(transaction.getTransactionHash(), block.getHeader().getIndex());

            // 将待打包交易池中包含此交易的记录删除，防止交易重复打包( fix bug for #IWSPJ)
            for (Iterator i = transactionPool.getTransactions().iterator(); i.hasNext();) {
                Transaction tx = (Transaction) i.next();
                if (tx.getTransactionHash().equals(transaction.getTransactionHash())) {
                    i.remove();
                }
            }

            //执行转账操作,更新账户余额
            sender.get().setBalance(sender.get().getBalance().subtract(transaction.getAmount()));
            recipient.get().setBalance(recipient.get().getBalance().add(transaction.getAmount()));
            dbAccess.putAccount(sender.get());
            dbAccess.putAccount(recipient.get());
        }// end for

        // 更新区块信息
        dbAccess.putBlock(block);
    }
}
