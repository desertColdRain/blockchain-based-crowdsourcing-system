package com.blockchain.mcsblockchain.service.impl.login;

import com.blockchain.mcsblockchain.Utils.Constants;
import com.blockchain.mcsblockchain.Utils.Cryptography;
import com.blockchain.mcsblockchain.Utils.ToHexUtils;
import com.blockchain.mcsblockchain.common.dto.LoginDto;
import com.blockchain.mcsblockchain.pojo.Return.Result;
import com.blockchain.mcsblockchain.pojo.account.Account;
import com.blockchain.mcsblockchain.pojo.db.DBAccess;
import com.blockchain.mcsblockchain.service.inter.login.LoginService;
import com.blockchain.mcsblockchain.web.vo.req.LoginVo;
import com.google.common.base.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
public class LoginServiceImpl implements LoginService {
    @Autowired
    DBAccess dbAccess;
    @Override
    public Result login(@RequestBody LoginDto loginDto) throws NoSuchAlgorithmException {

        Optional<Account> account = dbAccess.getAccount(loginDto.getUsername());
        if(!account.isPresent()) {
            System.out.println("账号不存在,用户名为："+loginDto.getUsername()+" 密码为："+loginDto.getPassword());
           return new Result("用户名或密码错误",Constants.CODE_FAIL_CLIENT,new LoginVo());
        }
        else{
            LoginVo loginVo = new LoginVo();
            if(account.get().getPassword().equals(loginDto.getPassword())) {
                System.out.println("登录成功");
                loginVo.setMsg("登陆成功");
                loginVo.setToken(UUID.randomUUID().toString());
                loginVo.setUsername(account.get().getUserName());
                loginVo.setPassword(Cryptography.myHash(account.get().getPassword()));
                loginVo.setPubKey(ToHexUtils.toHex(account.get().getPublicKey().value));
                loginVo.setPriKey(ToHexUtils.toHex(account.get().getSecretKey().value));
                loginVo.setAddress(account.get().getAccountAddr());
               return new Result("登录成功",Constants.CODE_SUCCESS,loginVo);
            }
            else{
                System.out.println("用户名或密码错误");
                return new Result("用户名或密码错误",Constants.CODE_FAIL_CLIENT,new LoginVo());
            }
        }

    }

    @Override
    public Result logout() {
        return new Result("退出成功",Constants.CODE_SUCCESS,null);
    }
}
