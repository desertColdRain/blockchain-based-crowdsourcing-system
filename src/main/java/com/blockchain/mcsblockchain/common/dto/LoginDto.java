package com.blockchain.mcsblockchain.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = true)
@Slf4j
@Data
public class LoginDto extends AbstractMethodError implements Serializable {


    private String username;
    private String password;


}
