package com.blockchain.mcsblockchain.common.dto;

import lombok.Data;

@Data
public class SubmitTaskDto {
    private int taskId;
    private String content;
    private String user;
}
