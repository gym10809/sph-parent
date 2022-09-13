package com.gm.gmall.common.feignClient.user;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.user.UserAddress;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/13 0013 20:10
 */
@RequestMapping("/api/inner/rpc/user")
@FeignClient("service-login")
public interface UserFeignClient {

    @GetMapping("/address/list")
     Result<List<UserAddress>> list();
}
