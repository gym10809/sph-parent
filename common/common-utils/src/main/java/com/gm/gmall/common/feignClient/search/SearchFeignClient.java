package com.gm.gmall.common.feignClient.search;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.list.Goods;
import com.gm.gmall.model.list.SearchParam;
import com.gm.gmall.model.list.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author gym
 * @create 2022/9/5 0005 10:14
 */
@RequestMapping("api/inner/rpc/search")
@FeignClient("service-search")
public interface SearchFeignClient {

    @PostMapping("/save")
     Result save(@RequestBody Goods goods);

    @DeleteMapping ("/delete/{skuId}")
    Result del(@PathVariable("skuId") Long skuId);

    @PostMapping("/search")
    Result<SearchResponseVo> search(@RequestBody SearchParam searchParam);
}
