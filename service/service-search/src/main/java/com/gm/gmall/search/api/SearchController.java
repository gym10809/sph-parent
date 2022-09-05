package com.gm.gmall.search.api;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.list.Goods;
import com.gm.gmall.model.list.SearchParam;
import com.gm.gmall.model.list.SearchResponseVo;
import com.gm.gmall.search.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author gym
 * @create 2022/9/5 0005 10:16
 */
@RestController
@RequestMapping("api/inner/rpc/search")
public class SearchController {

    @Autowired
    GoodsService goodsService;

    /**
     * 保存商品信息
     * @param goods
     * @return
     */
    @PostMapping("/save")
    public Result save(@RequestBody Goods goods){
        goodsService.save(goods);
        return Result.ok();
    }

    /**
     * 根据id删除某件商品
     * @param skuId
     * @return
     */
    @DeleteMapping("/delete/{skuId}")
    public Result del(@PathVariable("skuId") Long skuId){
        goodsService.delete(skuId);
        return Result.ok();
    }

    /**
     * 商品检索
     */
    @PostMapping("/search")
    public Result<SearchResponseVo> search(@RequestBody SearchParam searchParam){
       SearchResponseVo searchResponseVo=  goodsService.search(searchParam);
        return Result.ok(searchResponseVo);
    }
}
