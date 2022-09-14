package com.gm.gmall.common.feignClient.ware;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


/**
 * @author gym
 * @create 2022/9/13 0013 23:48
 */

@FeignClient(value = "ware-manage",url = "${app.ware-url:http://localhost:9002/}")
public interface WareFeignClient {
    @GetMapping("/hasStock")
    String hasStock(@RequestParam("skuId") Long skuId,
                    @RequestParam("num") Integer num);
}
