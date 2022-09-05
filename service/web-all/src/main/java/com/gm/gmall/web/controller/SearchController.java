package com.gm.gmall.web.controller;

import com.gm.gmall.common.feignClient.search.SearchFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.list.SearchParam;
import com.gm.gmall.model.list.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author gym
 * @create 2022/9/5 0005 10:45
 */
@Controller

public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;
    @GetMapping("/list.html")
    public String search(Model model, SearchParam searchParamVo){
        Result<SearchResponseVo> search = searchFeignClient.search(searchParamVo);
        SearchResponseVo data = search.getData();
        //返回传来的搜索信息
        model.addAttribute("searchParam",searchParamVo);
        //品牌列表
        model.addAttribute("trademarkList",data.getTrademarkList());
        //属性集合
        model.addAttribute("attrsList",data.getAttrsList());
        //排序规则
        model.addAttribute("orderMap",data.getOrderMap());
        //商品列表
        model.addAttribute("goodsList",data.getGoodsList());
        //分页信息
        model.addAttribute("pageNo",data.getPageNo());
        model.addAttribute("totalPages",data.getTotalPages());
        //url信息
        model.addAttribute("urlParam",data.getUrlParam());
        //属性面包屑
        model.addAttribute("propsParamList",data.getPropsParamList());
        //品牌面包屑
        model.addAttribute("trademarkParam",data.getTrademarkParam());
        return "list/index";
    }
}
