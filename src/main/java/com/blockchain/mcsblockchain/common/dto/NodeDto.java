package com.blockchain.mcsblockchain.common.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class NodeDto {
    private String ip;
    private int port;
    private String notes;

}
