package com.gm.gmall.product.init;

import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.product.service.SkuInfoService;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author gym
 * @create 2022/8/30 0030 16:48
 */
@Service
public class SkuIdBloomInit {


    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    RedissonClient redissonClient;

    @PostConstruct//对象创建成功以后就启动
    public void bloomInit(){
     List<Long> list= skuInfoService.getAllIds();
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter(RedisConstant.BLOOM_PRE);
        if (!bloomFilter.isExists())
        bloomFilter.tryInit(1000000,0.01);
        list.stream().forEach(id->{
            bloomFilter.add(id);
     });
    }
}
