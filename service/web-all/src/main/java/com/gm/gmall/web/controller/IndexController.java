package com.gm.gmall.web.controller;

import com.gm.gmall.common.feignClient.product.ProductFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.to.IndexTreeTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/26 0026 19:14
 */
@Controller

public class IndexController {

    @Autowired
    ProductFeignClient treeFeignClient;
    @GetMapping({"/", "/index"})
    public String index(Model model){
        Result<List<IndexTreeTo>> tree = treeFeignClient.tree();
        if (tree.isOk()){
            List<IndexTreeTo> data = tree.getData();
            model.addAttribute("list",data);
        }
        return "/index/index";
    }

}
