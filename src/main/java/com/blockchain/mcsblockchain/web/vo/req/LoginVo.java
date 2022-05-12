package com.blockchain.mcsblockchain.web.vo.req;

import com.blockchain.mcsblockchain.pojo.account.Account;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
public class LoginVo {

    //账户
    private String username;
    private String password;
    private String pubKey;
    private String priKey;
    private String address;
    //账户token，作为账户的识别
    private String token;
    //登录是否成功等
    private String msg;

    public LoginVo() {
    }

    public LoginVo(String username, String password, String pubKey, String priKey,
                   String address, String token, String msg) {
        this.username = username;
        this.password = password;
        this.pubKey = pubKey;
        this.priKey = priKey;
        this.address = address;
        this.token = token;
        this.msg = msg;
    }
}
