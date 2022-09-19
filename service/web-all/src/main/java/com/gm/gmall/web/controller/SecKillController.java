package com.gm.gmall.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author gym
 * @create 2022/9/19 0019 19:13
 */
@Controller
public class SecKillController {

    @GetMapping("/seckill.html")
    public String secKill(Model model){


        return "seckill/index";
    }
}
