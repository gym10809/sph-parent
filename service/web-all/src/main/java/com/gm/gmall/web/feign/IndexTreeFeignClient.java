package com.gm.gmall.web.feign;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.to.IndexTreeTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/26 0026 19:31
 */
@RequestMapping("api/inner/rpc/product")
@FeignClient("service-product")
public interface IndexTreeFeignClient {

    @GetMapping("/indexTree")
     Result<List<IndexTreeTo>> tree();
}
