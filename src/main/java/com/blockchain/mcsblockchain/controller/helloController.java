package com.blockchain.mcsblockchain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
class HelloController {

    @RequestMapping("/hello")
    public String hello() {
        return "This is an blockchain application";
    }

}
