package com.blockchain.mcsblockchain.service;


import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;

@Service
public class UserService {

    @Autowired
    DBAccess dbAccess;

    public Result register(String userName,String password){
        Result result = new Result();
        result.setSuccess(false);
        result.setDetail(null);
        try {
            Optional<Account> existUser = dbAccess.getAccount(userName);
            if(existUser.isPresent()){
                result.setMsg("该用户名已经存在");
            }
            else{
                Account account = Account.generateAccount();
                account.setUserName(userName);
                account.setPassword(password);
                dbAccess.putAccount(account);
                result.setMsg("注册账号成功");
                result.setSuccess(true);
                result.setDetail(account.toString());
            }
        } catch (NoSuchAlgorithmException e) {
            result.setMsg(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public Result login(String userName,String password){
        Result result = new Result();
        result.setSuccess(false);
        result.setDetail(null);
        try {
            Optional<Account> loginAccount = dbAccess.getAccount(userName);
            if(loginAccount.isPresent()){
                if(loginAccount.get().getPassword().equals(password)){
                    result.setSuccess(true);
                    result.setMsg("登陆成功");
                    result.setDetail(loginAccount.toString());
                }
                else{
                    result.setSuccess(false);
                    result.setMsg("账号不存在或密码错误");
                }

            }
            else{
                result.setSuccess(false);
                result.setMsg("账号不存在或密码错误");
            }
        } catch (NoSuchAlgorithmException e) {
            result.setMsg(e.getMessage());
            e.printStackTrace();
        }

        return result;
    }
}
