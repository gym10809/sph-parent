package com.gm.gmall.product.api;

import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.to.IndexTreeTo;
import com.gm.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gym
 * @create 2022/8/26 0026 19:30
 */
@RestController
@RequestMapping("api/inner/rpc/product")
public class IndexTreeController {
    @Autowired
    BaseCategory1Service baseCategory1Service;

    @GetMapping("/indexTree")
    public Result tree(){
        List<IndexTreeTo> list=baseCategory1Service.indexTree();
        return Result.ok(list);
    }
}
