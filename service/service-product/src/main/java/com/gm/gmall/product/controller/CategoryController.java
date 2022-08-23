package com.gm.gmall.product.controller;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.product.BaseCategory1;
import com.gm.gmall.model.product.BaseCategory2;
import com.gm.gmall.model.product.BaseCategory3;
import com.gm.gmall.product.service.BaseCategory1Service;
import com.gm.gmall.product.service.BaseCategory2Service;
import com.gm.gmall.product.service.BaseCategory3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/22 0022 20:33
 */
@RestController
@RequestMapping("/admin/product")
public class CategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;
    @Autowired
    BaseCategory2Service baseCategory2Service;
    @Autowired
    BaseCategory3Service baseCategory3Service;


    /**
     * 查询一级分类表单
     * @return
     */
    @GetMapping("/getCategory1")
    public Result getCategory1(){
        List<BaseCategory1> category1s = baseCategory1Service.list();
        return Result.ok(category1s);
    }

    /**
     * 根据一级分类中的id查询对应的二级分类
     * @param category1Id
     * @return
     */
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Integer category1Id){
        List<BaseCategory2> category2s = baseCategory2Service.getByCategoryId(category1Id);
        return Result.ok(category2s);
    }

    /**
     * 根据二级分类ID查询三级分类
     * @param category2Id
     * @return
     */
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") Integer category2Id){
      List<BaseCategory3> list=  baseCategory3Service.getByCategoryId(category2Id);
      return Result.ok(list);
    }



}
