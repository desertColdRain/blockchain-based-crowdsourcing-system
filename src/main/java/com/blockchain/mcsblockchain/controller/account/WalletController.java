package com.blockchain.mcsblockchain.controller.account;

import com.blockchain.mcsblockchain.common.dto.LoginDto;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.service.inter.login.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.security.NoSuchAlgorithmException;

@RestController
public class WalletController {

    @Autowired
    DBAccess dbAccess;

    @Autowired
    LoginService loginService;

    @CrossOrigin
    @PostMapping("/login")
    public Result login(@RequestBody LoginDto loginDto) throws NoSuchAlgorithmException {
       return loginService.login(loginDto);
    }

    @CrossOrigin
    @GetMapping("/logout")
    public Result logout(){
        return loginService.logout();
    }
}
