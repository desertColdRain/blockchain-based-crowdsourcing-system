package com.blockchain.mcsblockchain.service.impl.login;

import com.blockchain.mcsblockchain.Utils.Constants;
import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.common.dto.RegisterDto;
import com.blockchain.mcsblockchain.net.Socket.Client;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.service.impl.task.TaskServiceImpl;
import com.blockchain.mcsblockchain.service.inter.login.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    DBAccess dbAccess;
    @Autowired
    Client client;
    int port = 8888;
    String serverIp = "192.168.244.133";

    private static final Logger logger = LoggerFactory.getLogger(RegisterServiceImpl.class);

    @Override
    public Result register(@RequestBody RegisterDto registerDto) throws NoSuchAlgorithmException, IOException {

        logger.info("进入了注册界面");
        Account account = Account.generateAccount();

        account.setUserName(registerDto.getUsername());
        account.setPassword(registerDto.getPassword());
        account.setAccountType(registerDto.getAccountType());
        //send key registration signal to cloud server
        StringBuilder signal = new StringBuilder();
        //标识此信号为密钥认证信号
        signal.append("Z*");
        //1.miner公钥
        signal.append("[").append(account.getPublicKey().value).append("]").append("*");
        String message = account.getAccountAddr();
        Signature signature = Cryptography.SignAlgorithm(message, account.getSecretKey());
        System.out.println(signature.value);
        //2.miner的签名
        signal.append("[").append(signature.value).append("]").append("*");
        String enclavePublicKey = dbAccess.getEnclavePublicKey(DBAccess.ENCLAVE_PUBLIC_KEY);
        //3.签名用的message
        signal.append(account.getAccountAddr()).append("*");
        //4.enclave 公钥
        signal.append(enclavePublicKey).append("*");
        signal.append(account.getUserName());

        client.init(serverIp, port);
        System.out.println(signal);
        String ret = client.sendMessage(signal.toString());
        System.out.println("ret:" + ret);
        //此处留用为从sgx返回的信号，是否key 认证成功
        //认证成功
        if (ret.trim().equals("OK")) {
            if(dbAccess.getAccount(registerDto.getUsername()).isPresent()){
                logger.info("账号已经存在");
                return new Result("账号创建失败", Constants.CODE_FAIL_CLIENT,account.toString());
            }
            System.out.println("Access success");
            dbAccess.putAccount(account);
            client.closeClient();
            System.out.println(account);
            logger.info("账号创建成功");
            return new Result("账号创建成功", Constants.CODE_SUCCESS,account.toString());
        }
        //否则创建失败
        else {
            //System.out.println("sorry");
            client.closeClient();
            logger.info("账号创建失败，可能sgx检测到此公钥已被占用或者用户名已存在");
            return new Result("账号创建失败，可能sgx检测到此公钥已被占用或者用户名已存在",Constants.CODE_FAIL_CLIENT,account.toString());
        }

    }
}
