package com.blockchain.mcsblockchain.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.Utils.FastJson;
import com.blockchain.mcsblockchain.Utils.JsonVo;
import com.blockchain.mcsblockchain.net.Socket.Client;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.crypto.Signature;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.pojo.signal.KeyRegistrationSignal;
import com.blockchain.mcsblockchain.web.vo.res.AccountVo;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author yangjian
 * @since 18-4-8
 */
@RestController
@Api(tags = "Account API", description = "账户相关的 API")
@RequestMapping("/api/account")
public class AccountController {

	@Autowired
	private Account account;
	@Autowired
	private DBAccess dbAccess;
	@Autowired
	private Client client;
	private String serverIp = "192.168.244.132";
	private int port = 8888;

	/**
	 * 创建账户
	 * @return
	 */
	@ApiOperation(value="创建一个新的钱包账户")
	@ApiImplicitParams({
			@ApiImplicitParam(name="username",value="用户名",required = true,dataType="string",paramType = "query"),
			@ApiImplicitParam( name="accountType",value="账户类型(0代表矿工，1代表工人)",required = true,dataType="int",paramType = "query"),
			@ApiImplicitParam(name="password",value="密码",required = true,dataType="string",paramType = "query")
	})
	@PostMapping("/new_account")
	public String newAccount(@RequestParam("username") String username,
							 @RequestParam("password") String password,
							 @RequestParam("accountType") int accountType) throws Exception
	{

		Account account = Account.generateAccount();
		Preconditions.checkNotNull(username, "username is needed.");
		Preconditions.checkNotNull(password, "password is need.");
		account.setUserName(username);
		account.setPassword(password);
		account.setAccountType(accountType);
		AccountVo vo = new AccountVo();
		//send key registration signal to cloud server
		StringBuilder signal = new StringBuilder();
		//标识此信号为密钥认证信号
		signal.append("Z*");
		//1.miner公钥
		signal.append("[").append(account.getPublicKey().value).append("]").append("*");
		String message = account.getAccountAddr();
		Signature signature = Cryptography.SignAlgorithm(message, account.getSecretKey());
		System.out.println(signature.value);
		//2.miner的签名
		signal.append("[").append(signature.value).append("]").append("*");
		String enclavePublicKey = dbAccess.getEnclavePublicKey(DBAccess.ENCLAVE_PUBLIC_KEY);
		//3.签名用的message
		signal.append(account.getAccountAddr()).append("*");
		//4.enclave 公钥
		signal.append(enclavePublicKey).append("*");
		signal.append(account.getUserName());
		client.init(serverIp,port);
		System.out.println(signal);
		String ret = client.sendMessage(signal.toString());
		System.out.println("ret:"+ret);
		//此处留用为从sgx返回的信号，是否key 认证成功
		//认证成功
		if(ret.trim().equals("OK")){
			System.out.println("Access success");
			dbAccess.putAccount(account);
			client.closeClient();
			return account.toString();
		}
		//否则创建失败
		else{
			System.out.println("sorry");
			client.closeClient();
			return "Account generate failed. Because SGX find that the public key has emerged";
		}

	}

	@ApiOperation(value = "更新账号",notes = "根据用户名更新账户的公私钥对")
	@GetMapping("/key_alteration")
	@ApiImplicitParams({
			@ApiImplicitParam(name="username",value="用户名",required = true,dataType="string",paramType = "query"),
			@ApiImplicitParam(name="password",value="密码",required = true,dataType="string",paramType = "query")
	})
	public String keyAlteration(@RequestParam("username") String username,
								@RequestParam("password") String password) throws NoSuchAlgorithmException, IOException {
		Optional<Account> account = dbAccess.getAccount(username);
		Account newAccount = Account.generateAccount();
		if(!account.isPresent()){
			return "The account you print is not present! Please check username and password";
		}
		else if(!account.get().getPassword().equals(password)){
			return "Please enter the correct username or password";
		}
		else{
			// key alteration signal generate
			StringBuilder keyAlterationSignal = new StringBuilder();
			//标识此信号为密钥认证信号
			keyAlterationSignal.append("Y*");
			//1. old public key
			keyAlterationSignal.append("[").append(account.get().getPublicKey().value).append("]*");
			//2. new public key
			keyAlterationSignal.append("[").append(newAccount.getPublicKey().value).append("]").append("*");
			//3. signature
			Signature signature = Cryptography.SignAlgorithm(account.get().getPublicKey().value.toString() +
					newAccount.getPublicKey().value.toString(), account.get().getSecretKey());
			keyAlterationSignal.append("[").append(signature.value).append("]*");
			//4. signed message
			keyAlterationSignal.append(account.get().getPublicKey().value.toString()).append(newAccount.getPublicKey().value.toString()).append("*");
			//5. public key of enclave
			String enclavePublicKey = dbAccess.getEnclavePublicKey(DBAccess.ENCLAVE_PUBLIC_KEY);
			keyAlterationSignal.append(enclavePublicKey).append("*");
			//6. username
			keyAlterationSignal.append(username);

			client.init(serverIp,port);
			String ret = client.sendMessage(keyAlterationSignal.toString());

			if(Objects.equals(ret.trim(), "OK")){
				account.get().setPublicKey(newAccount.getPublicKey());
				account.get().setSecretKey(newAccount.getSecretKey());
				dbAccess.putAccount(account.get());
				return account.toString();
			}
			else{
				return "Sorry, alter keypair failed, maybe the public key you choosed have been used by others";
			}
		}

	}
	/**
	 * 获取挖矿账号
	 * @return
	 */
	@ApiOperation(value="获取挖矿钱包账号", notes = "获取挖矿钱包账号信息，包括地址，私钥，余额等信息")
	@GetMapping("/get_miner_address")

	public String getMinerAddress() throws NoSuchAlgorithmException {
		Optional<Account> minerAccount = dbAccess.getMinerAccount();
		JsonVo success = JsonVo.success();
		if (minerAccount.isPresent()) {
			success.setItem(minerAccount.get());
			System.out.println(success.getItem());
		} else {
			success.setMessage("Miner account is not created");
		}
		return minerAccount.toString();

	}

	/**
	 * 列出所有的账号
	 * @return
	 */
	@GetMapping("/list")
	@ApiOperation(value = "获取当前节点所有钱包账户")
	public String getAllAccounts() throws IOException, ClassNotFoundException {
		//List<Account> accounts = dbAccess.getAllAccounts();
		/*JsonVo success = JsonVo.success();
		for(Account a:accounts){
			System.out.print(" "+a);
		}
		success.setItem(accounts);*/
		return JSONArray.toJSONString(dbAccess.getAllAccounts());
	}
}
