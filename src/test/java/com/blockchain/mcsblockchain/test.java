package com.blockchain.mcsblockchain;

import com.blockchain.mcsblockchain.conf.AppConfig;
import com.blockchain.mcsblockchain.net.client.AppClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Component
public class test {

    @Autowired
    AppConfig appClient;

    @Test
    public void clientTest() throws Exception {
    }
}
