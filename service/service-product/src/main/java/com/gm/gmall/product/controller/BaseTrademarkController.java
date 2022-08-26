package com.gm.gmall.product.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.BaseTrademark;
import com.gm.gmall.product.service.BaseTrademarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/23 0023 21:08
 */
@RestController
@RequestMapping("admin/product")
public class BaseTrademarkController {
    @Autowired
    BaseTrademarkService baseTrademarkService;

    /**
     * 分页查询
     * @param pageNumber
     * @param limit
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result getPage(@PathVariable("page")Long pageNumber,@PathVariable("limit")Long limit){
        Page<BaseTrademark> page=new Page<>(pageNumber,limit);
        Page<BaseTrademark> trademarkPage = baseTrademarkService.page(page);
        return Result.ok(trademarkPage);
    }
    /**
     * 添加品牌
     */
    @PostMapping("/baseTrademark/save")
    public Result save(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }
    /**
     * 修改品牌
     */
    @PutMapping("/baseTrademark/update")
    public Result update(@RequestBody BaseTrademark baseTrademark){
        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }
    /**
     * 根据品牌id获取品牌
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getById(@PathVariable("id")Integer id){
        BaseTrademark baseTrademark = baseTrademarkService.getById(id);
        return Result.ok(baseTrademark);
    }
    /**
     * 根据品牌id删除品牌
     */
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result delete(@PathVariable("id")Integer id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }

    @GetMapping("/baseTrademark/getTrademarkList")
    public Result getTrademarkList(){
        List<BaseTrademark> list = baseTrademarkService.list();
        return Result.ok(list);
    }

}
