package com.blockchain.mcsblockchain;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.Utils.SerializeUtils;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.Transaction;
import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import com.blockchain.mcsblockchain.pojo.crypto.SKType;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import com.blockchain.mcsblockchain.pojo.db.RocksDBAccess;
import com.google.common.base.Optional;
import org.junit.jupiter.api.Test;
import org.rocksdb.RocksDBException;
import org.springframework.boot.test.context.SpringBootTest;


import java.io.IOException;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@SpringBootTest
class McsBlockchainApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, RocksDBException, IOException, ClassNotFoundException {
        Cryptography.init();
        String msg="Zheng Yan";
        SKType sk = new SKType();
        PKType pk = new PKType();
        pk.value.set(Cryptography.P);
        pk.value.powZn(sk.value);
        System.out.println("Pk: "+pk.value);
        System.out.println("Sk: "+sk.value);

        Signature sign = Cryptography.SignAlgorithm(msg, sk);
        System.out.println("signature: "+sign.value);
        System.out.println("Hash: "+Cryptography.myHash(msg));
        boolean flag = Cryptography.SignatureVerify(msg, pk, sign);
        System.out.println(flag);
        Account account = new Account();
        RocksDBAccess rock = new RocksDBAccess();
        rock.initRocksDB();


        account = Account.generateAccount();
        rock.putAccount(account);
        //Optional<Account> account1 = rock.getAccount(account.getAccountAddr());
        //System.out.println(account1.get());
        System.out.println("public key: "+account.getPublicKey().value);
        System.out.println("account address: "+account.getAccountAddr());
        List<Account> allAccounts = rock.getAllAccounts();
        System.out.println("总共有"+allAccounts.size()+"个账号");
       for(Account a:allAccounts){
           System.out.println("all accounts: "+a);
       }
       rock.closeDB();

    }

}
