package com.blockchain.mcsblockchain.service.inter.task;

import com.blockchain.mcsblockchain.common.dto.ApplyDto;
import com.blockchain.mcsblockchain.common.dto.SubmitTaskDto;
import com.blockchain.mcsblockchain.common.dto.TaskDto;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.core.Task;
import com.blockchain.mcsblockchain.pojo.core.TaskContent;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface TaskService {

    Result viewTask() throws NoSuchAlgorithmException;
    Result applyTask(ApplyDto applyDto) throws NoSuchAlgorithmException, IOException;
    Result issueTask(TaskDto taskDto) throws Exception;
    // taskId 要提交的任务的id，taskContent要提交的任务的答案
    Result submitTask(SubmitTaskDto submitTaskDto);
}
