package com.blockchain.mcsblockchain.service.impl.task;

import com.blockchain.mcsblockchain.Utils.Constants;
import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.common.dto.ApplyDto;
import com.blockchain.mcsblockchain.common.dto.SubmitTaskDto;
import com.blockchain.mcsblockchain.common.dto.TaskDto;
import com.blockchain.mcsblockchain.conf.AppConfig;
import com.blockchain.mcsblockchain.net.Socket.Client;
import com.blockchain.mcsblockchain.net.client.AppClient;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.Blockchain;
import com.blockchain.mcsblockchain.pojo.core.Task;
import com.blockchain.mcsblockchain.pojo.core.Transaction;
import com.blockchain.mcsblockchain.pojo.core.TransactionPool;
import com.blockchain.mcsblockchain.pojo.crypto.PKType;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.service.inter.task.TaskService;
import com.google.common.base.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.bcel.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    int port = 8888;
    String serverIp = "192.168.244.133";

    @Autowired
    DBAccess dbAccess;

    @Autowired
    Client client;

    @Autowired
    Blockchain blockchain;

    @Autowired
    AppConfig appConfig;

    @Autowired
    TransactionPool transactionPool;

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Override
    public Result viewTask() {
        //获取最新的任务id
        logger.info("进入查看任务环节");
        Optional<Object> lastTaskId = dbAccess.getLastTaskId();
        if (!lastTaskId.isPresent()) {
            logger.info("目前还没有已发布的任务");
            return new Result("目前还没有已发布的任务", Constants.CODE_FAIL_SERVER, null);
        } else {
            List<Task> tasks = new ArrayList<>();
            for (int i = 1; i <= (int) lastTaskId.get(); i++) {
                Optional<Task> task = dbAccess.getTask(i);
                if (task.isPresent()) {
                    tasks.add(task.get());
                }
            }
            logger.info("查询任务成功");
            return new Result("查询任务成功", Constants.CODE_SUCCESS, tasks);
        }

    }

    @Override
    public Result applyTask(ApplyDto applyDto) throws NoSuchAlgorithmException, IOException {
        Optional<Task> task = dbAccess.getTask(applyDto.getTaskId());
        System.out.println("进入申请任务环节");
        logger.info("进入申请任务环节...");
        logger.info("任务id为{}",applyDto.getTaskId());
        logger.info("用户名为：{}",applyDto.getUsername());
        //任务不存在
        if(!task.isPresent()){
            logger.info("您要申请的任务不存在");
            return new Result("您要申请的任务不存在,请检查您的输入", Constants.CODE_FAIL_CLIENT,null);
        }

        else{

            Optional<Account> account = dbAccess.getAccount(applyDto.getUsername());
            //账号不存在
            if(!account.isPresent()){
                logger.info("您输入的账号不存在......");
                return new Result("您输入的账号不存在",Constants.CODE_FAIL_CLIENT,null);
            }
            else{
                String contentsHash = Cryptography.myHash(task.get().getTaskContent().toString());
                PKType publicKey = account.get().getPublicKey();
                Signature signature = Cryptography.SignAlgorithm(contentsHash, account.get().getSecretKey());
                StringBuilder signal = new StringBuilder();
                //以A*开头代表这是一个申请任务的signal
                signal.append("A*");
                //1. 要申请任务内容的哈希值
                signal.append(contentsHash ).append("*");
                //2. 申请人的公钥
                signal.append("[").append(publicKey.value).append("]").append("*");
                //3. 申请人对于1的内容的签名
                signal.append("[").append(signature.value).append("]").append("*");
                //4. 任务要求的信任值
                signal.append(task.get().getTrustRequirement()).append("*");
                //5. 用户名
                signal.append(applyDto.getUsername());
                logger.info("向服务器发送申请");
                client.init(serverIp, port);
                System.out.println(signal);
                String ret = client.sendMessage(signal.toString());
                System.out.println("ret:" + ret);
                //信任值符合条件，可以申请任务
                if (!ret.trim().equals("NO")) {
                    logger.info("任务申请成功");
                    task.get().addUser(applyDto.getUsername());
                    dbAccess.putTask(task.get());
                   return new Result("您好，经查询您的信任值符合条件，申请任务成功",Constants.CODE_SUCCESS,ret);
                }
                //否则创建失败
                else {
                    logger.info("任务申请失败，信任值不符合条件");
                   return new Result("您好，您的信任值不符合条件，请提升信任值之后再申请此类任务哦",Constants.CODE_FAIL_CLIENT,null);
                }
            }


        }
    }

    @Override
    public Result issueTask(@RequestBody TaskDto taskDto) throws Exception {

        logger.info("进入发布任务环节");
        Optional<Account> tmp = dbAccess.getAccount(taskDto.getUsername());

        if (tmp.isPresent()) {
            //用户名和密码对不上
            if (!tmp.get().getPassword().equals(taskDto.getPassword())) {
                logger.info("用户名或密码不正确");
                return new Result("用户名或密码不正确", Constants.CODE_FAIL_CLIENT, null);
            }
            //用户名和密码正确
            else {
                Task task = new Task();
                task.setSenderUsername(taskDto.getUsername());
                //设置任务发布人的公钥
                task.setSenderPk(tmp.get().getPublicKey());
                task.setTaskContent(taskDto.getTaskContent());
                task.setTrustRequirement(taskDto.getTrustRequirement());
                task.setSenderSign(Cryptography.SignAlgorithm(taskDto.getTaskContent().toString(), tmp.get().getSecretKey()));

                Optional<Object> lastTaskId = dbAccess.getLastTaskId();
                //判断是第几个任务
                if (lastTaskId.isPresent()) {
                    task.setTaskId((int) lastTaskId.get()+1);
                    dbAccess.putTaskId((int) lastTaskId.get() + 1);
                } else {
                    task.setTaskId(1);
                    dbAccess.putTaskId(1);
                }

                //留观查看 task.setTaskId（）；
                Transaction transaction = blockchain.sendTransaction(
                        tmp.get(),
                        tmp.get().getPublicKey(),
                        task.getTaskContent().getBonus(),
                        task.getTaskContent().getRequirements());

                System.out.println("send transaction 之后的transaction："+transaction);
                //如果开启了自动挖矿，则直接自动挖矿
                if (appConfig.isAutoMining()) {
                    blockchain.mining();
                }
                dbAccess.putTask(task);
                transactionPool.getTransactions().clear();
                logger.info("发布任务成功");

                return new Result("发布任务成功", Constants.CODE_SUCCESS, task);
            }
        }
        //账号不存在
        else {
            logger.info("用户名或密码不正确，请检查您的输入");
            return new Result("用户名或密码不正确，请检查您的输入", Constants.CODE_FAIL_CLIENT, null);
        }
    }

    @Override
    public Result submitTask(SubmitTaskDto submitTaskDto) {
        logger.info("进入提交任务环节");
        logger.info(submitTaskDto.toString());
        //根据任务id找到相应的任务，首先查看任务是否存在，存在的话查看是否用户已经申请过任务
        Optional<Task> task = dbAccess.getTask(submitTaskDto.getTaskId());
        if(!task.isPresent()){
            return new Result("您要做的任务并不存在",Constants.CODE_FAIL_CLIENT,null);
        }
        else{
            //用户并未申请过任务，即未经过匿名认证
            if(!task.get().getUser().contains(submitTaskDto.getUser())){
                return new Result("抱歉，您还没有申请此任务，请申请获得资格之后再做任务",Constants.CODE_FAIL_CLIENT,null);

            }
            //用户已经申请过任务
            else{

                return new Result("恭喜，提交任务成功",Constants.CODE_SUCCESS,submitTaskDto);
            }
        }

    }
}
