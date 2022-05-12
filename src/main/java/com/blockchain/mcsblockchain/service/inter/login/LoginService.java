package com.blockchain.mcsblockchain.service.inter.login;

import com.blockchain.mcsblockchain.common.dto.LoginDto;
import com.blockchain.mcsblockchain.pojo.Return.Result;

import java.security.NoSuchAlgorithmException;

public interface LoginService {

    Result login(LoginDto loginDto) throws NoSuchAlgorithmException;
    Result logout();
}
