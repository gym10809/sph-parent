package com.gm.gmall.login.controller;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.login.service.UserInfoService;
import com.gm.gmall.model.vo.LoginSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gym
 * @create 2022/9/6 0006 20:10
 */
@RestController
@RequestMapping("/api/user")
public class LoginController {

    @Autowired
    UserInfoService userInfoService;
    @PostMapping("/passport/login")
    public Result login(@RequestParam("loginName")String loginName,@RequestParam("passwd")String passwd){
      LoginSuccessVo vo= userInfoService.login(loginName,passwd);
        if (vo==null){
            //登录失败
            return Result.build("", ResultCodeEnum.LOGIN_ERRO);
        }
        return Result.ok(vo);
    }
}
