package com.gm.gmall.web.controller;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.to.SkuDetailTo;
import com.gm.gmall.web.feign.ItemFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author gym
 * @create 2022/8/26 0026 20:15
 */
@Controller
public class ItemController {
    @Autowired
    ItemFeignClient itemFeignClient;

    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId")Integer skuId, Model model){
     Result<SkuDetailTo> result= itemFeignClient.getDetail(skuId);
   if (result.isOk()){
       SkuDetailTo skuDetailTo = result.getData();
       if (skuDetailTo ==null || skuDetailTo.getSkuInfo()==null){
           return "item/error";
       }
           model.addAttribute("categoryView",skuDetailTo.getCategoryView());
           model.addAttribute("skuInfo",skuDetailTo.getSkuInfo());
           model.addAttribute("price",skuDetailTo.getPrice());
           model.addAttribute("spuSaleAttrList",skuDetailTo.getSpuSaleAttrList());
           model.addAttribute("valuesSkuJson",skuDetailTo.getValuesSkuJson());//json
   }
   return "item/index";

    }
}
