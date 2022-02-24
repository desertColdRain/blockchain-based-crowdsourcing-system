package com.blockchain.mcsblockchain.web.controller.api;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blockchain.mcsblockchain.Utils.FastJson;
import com.blockchain.mcsblockchain.Utils.JsonVo;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.web.vo.res.AccountVo;
import com.google.common.base.Optional;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

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

	/**
	 * 创建账户
	 * @return
	 */
	@ApiOperation(value="创建一个新的钱包账户")
	@GetMapping("/new_account")
	public JsonVo newAccount() throws Exception
	{

		Account account = Account.generateAccount();
		AccountVo vo = new AccountVo();
		BeanUtils.copyProperties(account, vo);
		return new JsonVo(JsonVo.CODE_SUCCESS,
				"New account created, please remember your Address and Private Key.",
				vo);
	}

	/**
	 * 获取挖矿账号
	 * @return
	 */
	@ApiOperation(value="获取挖矿钱包账号", notes = "获取挖矿钱包账号信息，包括地址，私钥，余额等信息")
	@GetMapping("/get_miner_address")

	public JsonVo getMinerAddress() throws NoSuchAlgorithmException {
		Optional<Account> minerAccount = dbAccess.getMinerAccount();
		JsonVo success = JsonVo.success();
		if (minerAccount.isPresent()) {
			success.setItem(minerAccount.get());
			System.out.println(success.getItem());
		} else {
			success.setMessage("Miner account is not created");
		}
		return success;

	}

	/**
	 * 列出所有的账号
	 * @return
	 */
	@GetMapping("/list")
	@ApiOperation(value = "获取当前节点所有钱包账户")
	public JsonVo getAllAccounts() throws IOException, ClassNotFoundException {
		List<Account> accounts = dbAccess.getAllAccounts();
		JsonVo success = JsonVo.success();
		for(Account a:accounts){
			System.out.print(" "+a);
		}
		success.setItem(accounts);
		return success;
	}
}
