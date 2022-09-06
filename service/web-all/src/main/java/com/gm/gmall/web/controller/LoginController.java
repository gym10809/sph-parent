package com.gm.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author gym
 * @create 2022/9/6 0006 19:54
 */
@Controller
public class LoginController {

    @GetMapping("/login.html")
    public  String login(@RequestParam("originUrl")String originUrl){

        return "login";
    }
}
