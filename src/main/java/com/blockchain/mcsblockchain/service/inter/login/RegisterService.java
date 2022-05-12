package com.blockchain.mcsblockchain.service.inter.login;

import com.blockchain.mcsblockchain.common.dto.RegisterDto;
import com.blockchain.mcsblockchain.pojo.Return.Result;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface RegisterService {

    Result register(RegisterDto registerDto) throws NoSuchAlgorithmException, IOException;
}
