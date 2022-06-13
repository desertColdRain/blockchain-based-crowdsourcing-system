package com.blockchain.mcsblockchain.common.dto;

import lombok.Data;

@Data
public class TrustEvaluateDto {
    //评分的对象
    private String username;
    //分数
    private float score;
}
