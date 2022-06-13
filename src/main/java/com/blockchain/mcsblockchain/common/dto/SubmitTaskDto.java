package com.blockchain.mcsblockchain.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@EqualsAndHashCode()
@Slf4j
@Data
public class SubmitTaskDto implements Serializable {
    private int taskId;
    private String content;
    private String username;
}
