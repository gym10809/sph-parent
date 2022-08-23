package com.gm.gmall.product.controller;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.BaseAttrInfo;
import com.gm.gmall.model.product.BaseAttrValue;
import com.gm.gmall.product.service.BaseAttrInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/23 0023 10:10
 */
@RestController
@RequestMapping("/admin/product")
public class BaseAttrController {
    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    /**
     * 根据三个分类分类查询对应的平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @GetMapping("/attrInfoList/{category1Id}/{category2Id}/{category3Id}")
    public Result getAttrInfoList(@PathVariable("category1Id") Integer category1Id
                                     , @PathVariable("category2Id")Integer category2Id,
                                     @PathVariable("category3Id")Integer category3Id){
        List<BaseAttrInfo> list= baseAttrInfoService.getInfoList(category1Id,category2Id,category3Id);
        return Result.ok(list);
    }

    /**
     * 新增或修改属性对应的属性值
     * @param baseAttrInfo
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        baseAttrInfoService.saveAttrInfo(baseAttrInfo);
        return Result.ok();
    }

    /**
     * 通过平台属性id获取对应的属性值
     * @param attrId
     * @return
     */
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId")Integer attrId ){
        List<BaseAttrValue> list=baseAttrInfoService.getAttrValue(attrId);

        return Result.ok(list);
    }


}
