package com.blockchain.mcsblockchain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.blockchain.mcsblockchain.Utils.FastJson;
import com.blockchain.mcsblockchain.conf.AppConfig;
import com.blockchain.mcsblockchain.net.base.Node;
import com.blockchain.mcsblockchain.pojo.core.Block;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.google.common.base.Optional;
import org.apache.catalina.util.ToStringUtil;
import org.junit.jupiter.api.Test;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class McsBlockchainApplicationTests {


    private DBAccess dbAccess;


    @Test
    void contextLoads() {

    }

    public static void main(String[] args) {

    }
    @Test
    public void client() throws Exception {

       /* Account account=Account.generateAccount();
        dbAccess.putAccount(account);
        List<Account> allAccounts = dbAccess.getAllAccounts();
        for(Account a:allAccounts){
            System.out.println("Accounts: "+a);
        }*/
        Optional<Block> block = dbAccess.getLastBlock();
        if(block.isPresent()){
            System.out.println("区块头信息： "+block.get().getHeader()+"   区块体信息："+block.get().getBody());
        }
        else
        {
            System.out.println("数据库中不存在关于区块的信息");
        }

    }

}
