package com.blockchain.mcsblockchain.controller.account;

import com.blockchain.mcsblockchain.common.dto.RegisterDto;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.service.inter.login.RegisterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class RegisterController {

    @Autowired
    DBAccess dbAccess;

    @Autowired
    RegisterService registerService;

    @CrossOrigin
    @PostMapping("/register")
    //@RequestBody 接收来自前端的数据
    public Result register(@RequestBody RegisterDto registerDto) throws NoSuchAlgorithmException, IOException {
        return registerService.register(registerDto);
    }
}
