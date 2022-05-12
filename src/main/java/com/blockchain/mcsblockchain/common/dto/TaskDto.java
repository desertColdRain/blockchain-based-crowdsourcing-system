package com.blockchain.mcsblockchain.common.dto;

import com.blockchain.mcsblockchain.pojo.core.TaskContent;
import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TaskDto {

    private String username;            //任务发送者用户名
    private String password;
    private String address;             //任务发布者的地址
    private String trustRequirement;    //申请任务需要达到的信任值
    private TaskContent taskContent;


}
