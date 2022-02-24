package com.blockchain.mcsblockchain;

import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.core.Transaction;
import com.blockchain.mcsblockchain.pojo.core.TransactionPool;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.google.common.base.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class McsBlockchainApplicationTests {


    @Autowired
    private DBAccess dbAccess;


    @Test
    void contextLoads() {

    }

    public static void main(String[] args) {

    }
    @Test
    public void client() throws Exception {

        List<Node> nodes=new ArrayList<>();
        nodes.add(new Node("127.0.0.1",5678));
        nodes.add(new Node("127.0.0.1",6789));
        nodes.add(new Node("127.0.0.1",7890));

        Account account = Account.generateAccount();
        account.setUserName("desert");
        account.setPassword("fang962464");
        dbAccess.putAccount(account);
        dbAccess.setMinerAccount(account);
        Optional<Account> desert = dbAccess.getAccount("desert");
        Optional<Account> minerAccount = dbAccess.getMinerAccount();
        if(minerAccount.isPresent()) System.out.println("miner account: "+minerAccount.get());
        if(desert.isPresent()) System.out.println("account :" +desert.get());
        Optional<List<Node>> nodeList = dbAccess.getNodeList();
        if(nodeList.isPresent()){
            for(Node n:nodeList.get()){
                System.out.println(n);
            }
        }
        Optional<Block> block = dbAccess.getLastBlock();
        if(block.isPresent()){
            System.out.println(block);
        }

    }

}
