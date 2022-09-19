package com.gm.gmall.seckill.api;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.activity.SeckillGoods;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gym
 * @create 2022/9/19 0019 19:35
 */
@RestController
@RequestMapping("/api/inner/rpc/seckill")
public class SecKillApiController {

    @GetMapping("/list")
    public Result<SeckillGoods> list(){

        return Result.ok();
    }
}
