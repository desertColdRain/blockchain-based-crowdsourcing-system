package com.blockchain.mcsblockchain;

import com.blockchain.mcsblockchain.net.client.AppClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;


@SpringBootApplication

public class McsBlockchainApplication {

    @Autowired
    AppClient appClient;
    public static void main(String[] args) {

        SpringApplication.run(McsBlockchainApplication.class, args);


    }

}
