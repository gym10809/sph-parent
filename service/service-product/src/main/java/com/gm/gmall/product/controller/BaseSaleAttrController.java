package com.gm.gmall.product.controller;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.BaseSaleAttr;
import com.gm.gmall.product.service.BaseSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/25 0025 15:28
 */
@RestController
@RequestMapping("admin/product")
public class BaseSaleAttrController {

    @Autowired
    BaseSaleAttrService baseSaleAttrService;
    @GetMapping("/baseSaleAttrList")
    public Result baseSaleAttrList(){
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }
}
