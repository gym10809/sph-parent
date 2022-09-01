package com.gm.gmall.product.rebuild.bloom;

import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.product.service.SkuInfoService;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/1 0001 18:09
 */
@RestController
@RequestMapping("/rebuild")
public class RebuildBloom {
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    SkuInfoService skuInfoService;
    @Scheduled(cron ="0 0 3 * * 3" )
    @RequestMapping("/bloom/now")
    public void buildBloom(){
        RBloomFilter<Object> oldBloom = redissonClient.getBloomFilter(RedisConstant.BLOOM_PRE);
        List<Integer> allIds = skuInfoService.getAllIds();

        //构建新的布隆过滤器，更新数据库的数据
        String newName=RedisConstant.BLOOM_PRE+"_new";
        RBloomFilter<Object> newBloom = redissonClient.getBloomFilter(newName);
        for (Integer id : allIds) {
            newBloom.add(id);
        }
        //删除旧的过滤器，上线新的过滤器
        oldBloom.rename("del");
        newBloom.rename(RedisConstant.BLOOM_PRE);

        oldBloom.deleteAsync();
    }
}
