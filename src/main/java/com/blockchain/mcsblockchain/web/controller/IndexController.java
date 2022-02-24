package com.blockchain.mcsblockchain.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Liu Guocang
 * @since 19-6-8 下午1:29
 */
@Controller
public class IndexController {

	@GetMapping("/hello")
	public String hello() {
		return "Hello blockchain.";
	}
}
