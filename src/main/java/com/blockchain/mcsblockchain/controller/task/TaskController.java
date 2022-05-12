package com.blockchain.mcsblockchain.controller.task;

import com.blockchain.mcsblockchain.common.dto.ApplyDto;
import com.blockchain.mcsblockchain.common.dto.SubmitTaskDto;
import com.blockchain.mcsblockchain.common.dto.TaskDto;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.service.inter.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@RestController
public class TaskController {
    @Autowired
    TaskService taskService;

    @CrossOrigin
    @GetMapping("task/view")
    public Result viewTask() throws NoSuchAlgorithmException {
        return taskService.viewTask();
    }

    @CrossOrigin
    @RequestMapping("task/issue")
    public Result issueTask(@RequestBody TaskDto taskDto) throws Exception {
        return taskService.issueTask(taskDto);
    }

    @CrossOrigin
    @PostMapping("task/apply")
    public Result applyTask(@RequestBody ApplyDto applyDto) throws NoSuchAlgorithmException, IOException {
        return taskService.applyTask(applyDto);
    }

    @CrossOrigin
    @PostMapping("task/submit")
    public Result submitTask(@RequestBody SubmitTaskDto submitTaskDto) throws NoSuchAlgorithmException {
        return taskService.submitTask(submitTaskDto);
    }
}
