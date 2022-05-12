package com.blockchain.mcsblockchain.web.controller.api;

import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.Utils.JsonVo;
import com.blockchain.mcsblockchain.conf.AppConfig;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.core.*;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import net.bytebuddy.build.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yangjian
 */
@RestController
@RequestMapping("/api/transaction")
@Api(tags = "Transaction API", description = "交易相关 API")
public class TransactionController {

	@Autowired
	private DBAccess dbAccess;
	@Autowired
	private Blockchain blockChain;
	@Autowired
	private AppConfig appConfig;
	@Autowired
	private TransactionPool transactionPool;
	/**
	 * 发送交易
	 * 1. 发布一个任务，发送一个交易，交易类型为1，这种交易应该是人人都可以看见的，应该设置一个查询交易类型为1
	 * 的接口
	 * 2. 提交一个任务，发送一个交易，交易类型为2，提交任务需要经过匿名认证，并且需要指定接收人，然后接收人
	 给出相应的分数

	 *
	 */
	@ApiOperation(value="发布任务", notes="发起一个任务")
	@ApiImplicitParams({
			@ApiImplicitParam(name="username",value="用户名",required = true,dataType="string",paramType = "query"),
			@ApiImplicitParam(name="password",value="密码",required = true,dataType="string",paramType = "query"),
			@ApiImplicitParam(name="task content",value="任务内容",required = true,dataType="TaskContent",paramType = "query"),
			@ApiImplicitParam(name="bonus",value = "奖金",required = true,dataType = "BigDecimal",paramType = "query")
	})
	@PostMapping("/send_transaction")
	public JsonVo sendTransaction(@RequestParam("username") String username,
								  @RequestParam("password") String password,
								  @RequestParam("task content") String taskContent,
								  @RequestParam("bonus") BigDecimal bonus) throws Exception {
		Preconditions.checkNotNull(username, "Your username is needed.");
		Preconditions.checkNotNull(password, "Password is needed.");
		Optional<Account> tmp = dbAccess.getAccount(username);
		if(tmp.isPresent()){
			//用户名和密码对不上
			if(!tmp.get().getPassword().equals(password)){
				JsonVo jsonVo = new JsonVo();
				jsonVo.setCode(JsonVo.CODE_FAIL);
				jsonVo.setMessage("Sorry, the username or password is incorrect");
				return jsonVo;
			}
			//用户名和密码正确
			else{
				Task task = new Task();
				task.setSenderUsername(username);
				//设置任务发布人的公钥
				task.setSenderPk(tmp.get().getPublicKey());
				SimpleDateFormat sdf = new SimpleDateFormat();
				sdf.applyPattern("yyyy年MM月dd HH:mm:ss");
				Date date = new Date();
				String format = sdf.format(date);
				task.setTaskContent(new TaskContent(taskContent,bonus,format));
				task.setSenderSign(Cryptography.SignAlgorithm(taskContent, tmp.get().getSecretKey()));
				Optional<Object> lastTaskId = dbAccess.getLastTaskId();
				if(lastTaskId.isPresent()){
					task.setTaskId((int)lastTaskId.get());
					dbAccess.putTaskId((int)lastTaskId.get()+1);
				}
				else {
					task.setTaskId(1);
					dbAccess.putTaskId(1);
				}
				//留观查看 task.setTaskId（）；
				/*Account credentials=new Account();
				credentials.setSecretKey(txVo.getPriKey());
				credentials.setBalance(txVo.getAmount());

				// 验证余额
				Optional<Account> account = dbAccess.getAccount(credentials.getAccountAddr());
				if (!account.isPresent()) {
					return JsonVo.instance(JsonVo.CODE_FAIL, "账户余额不足");
				}
				if (account.get().getBalance().compareTo(txVo.getAmount()) < 0) {
					return JsonVo.instance(JsonVo.CODE_FAIL, "账户余额不足");
				}*/
				Transaction transaction = blockChain.sendTransaction(
						tmp.get(),
						tmp.get().getPublicKey(),
						task.getTaskContent().getBonus(),
						task.getTaskContent().getRequirements());

				//如果开启了自动挖矿，则直接自动挖矿
				if (appConfig.isAutoMining()) {
					blockChain.mining();
				}
				dbAccess.putTask(task);

				JsonVo success = JsonVo.success();
				success.setItem(transaction.getTransactionHash());
				return success;
			}
		}
		//账号不存在
		else{
			JsonVo jsonVo = new JsonVo();
			jsonVo.setCode(JsonVo.CODE_FAIL);
			jsonVo.setMessage("你输入的账号不存在，请检查您的输入");
			return jsonVo;
		}
		//Credentials credentials = Credentials.create(txVo.getPriKey());

	}


	@ApiOperation(value = "提交任务",notes = "提交任务数据")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "username", value = "用户名", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "string", paramType = "query"),
			@ApiImplicitParam(name = "task data", value = "任务答案", required = true, dataType = "TaskContent", paramType = "query"),
			@ApiImplicitParam(name = "task id",value = "任务id",required = true,dataType = "int",paramType = "query")
	})
	@PostMapping("/commit_task")
	public JsonVo commitTask(@RequestParam("username") String username,
							 @RequestParam("password") String password,
							 @RequestParam("task data") String taskData,
							 @RequestParam("task id") int taskId) throws Exception {
		Optional<Account> account = dbAccess.getAccount(username);
		if(account.isPresent()){
			if(!account.get().getPassword().equals(password))
				return new JsonVo(JsonVo.CODE_FAIL,"您所输入的用户名和密码不正确");
			Optional<Task> task = dbAccess.getTask(taskId);
			if(!task.isPresent()) {
				return new JsonVo(JsonVo.CODE_FAIL,"您所输入的任务不存在，无法进行提交");
			}
			else {
				Optional<Account> tmp = dbAccess.getAccount(task.get().getSenderUsername());
				Transaction transaction = null;
				if(tmp.isPresent()){
					 transaction = blockChain.sendTransaction(
							account.get(),
							tmp.get().getPublicKey(),
							task.get().getTaskContent().getBonus(),
							taskData);
				}
				else{
					return new JsonVo(JsonVo.CODE_FAIL,"找不到账户的用户，无法进行提交");
				}


				//如果开启了自动挖矿，则直接自动挖矿
				if (appConfig.isAutoMining()) {
					blockChain.mining();
				}
				JsonVo success = JsonVo.success();
				success.setItem(transaction.getTransactionHash());
				return success;
			}

		}
		else{
			return new JsonVo(JsonVo.CODE_FAIL,"您所输入的用户名不存在");
		}

	}





	/**
	 * 根据交易哈希查询交易状态
	 * @param txHash
	 * @return
	 */
	@ApiOperation(value="查询交易状态", notes="根据交易哈希查询交易状态")
	@ApiImplicitParam(name = "txHash", value = "交易哈希", required = true, dataType = "String")
	@GetMapping("/get_transaction")
	public JsonVo getTransactionByTxHash(String txHash)
	{
		Preconditions.checkNotNull(txHash, "txHash is needed.");
		JsonVo success = JsonVo.success();
		// 查询交易池
		for (Transaction tx: transactionPool.getTransactions()) {
			if (txHash.equals(tx.getTransactionHash())) {
				success.setItem(tx);
				return success;
			}
		}
		// 查询区块
		Transaction transaction = dbAccess.getTransactionByTxHash(txHash);
		if (null != transaction) {
			success.setItem(transaction);
			return success;
		}

		success.setCode(JsonVo.CODE_FAIL);
		return success;
	}

}
