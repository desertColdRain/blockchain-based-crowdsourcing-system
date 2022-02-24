package com.blockchain.mcsblockchain.controller;

import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value="/register")
    public Result register(String userName,String password){
        return userService.register(userName,password);
    }

    @PostMapping(value="/login")
    public Result login(String userName,String password){
        return userService.login(userName,password);
    }
}
