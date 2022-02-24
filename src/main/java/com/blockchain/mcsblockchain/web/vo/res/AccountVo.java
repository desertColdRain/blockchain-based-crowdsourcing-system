package com.blockchain.mcsblockchain.web.vo.res;

import com.blockchain.mcsblockchain.pojo.account.Account;

import java.security.NoSuchAlgorithmException;


/**
 * account VO
 * @author yangjian
 * @since 18-7-14
 */
public class AccountVo extends Account {


	public AccountVo() throws NoSuchAlgorithmException {
	}

	@Override
	public String toString() {
		return "AccountVo{" +
				"address='" + getAccountAddr() + '\'' +
				", priKey='" + getSecretKey() + '\'' +
				'}';
	}
}
