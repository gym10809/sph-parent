package com.gm.gmall.login.api;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gm.gmall.common.auth.AuthUtils;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.login.service.UserAddressService;
import com.gm.gmall.model.user.UserAddress;
import com.gm.gmall.model.vo.user.UserInfoId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/13 0013 19:55
 */
@RestController
@RequestMapping("/api/inner/rpc/user")
public class ApiUserController {

    @Autowired
    UserAddressService addressService;
    @GetMapping("/address/list")
    public Result<List<UserAddress>> list(){
        UserInfoId info = AuthUtils.getInfo();
        Long userId = info.getUserId();
        LambdaQueryWrapper<UserAddress> queryWrapper=new LambdaQueryWrapper();
        queryWrapper.eq(UserAddress::getUserId,userId);
        List<UserAddress> list = addressService.list(queryWrapper);
        return Result.ok(list);

    }
}
