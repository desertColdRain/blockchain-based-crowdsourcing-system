package com.blockchain.mcsblockchain.common.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class RegisterDto {
    private String username;
    private String password;
    private int accountType;
}
