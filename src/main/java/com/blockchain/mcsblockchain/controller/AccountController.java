package com.blockchain.mcsblockchain.controller;


import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.pojo.account.Account;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;

@RestController
public class AccountController {
@RequestMapping("/login")
    public String newAccount() throws NoSuchAlgorithmException {
    Cryptography.init();
    Account account = new Account();
    account=Account.generateAccount();
    return account.toString();
}

}
