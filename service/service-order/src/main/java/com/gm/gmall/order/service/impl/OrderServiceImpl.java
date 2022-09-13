package com.gm.gmall.order.service.impl;

import com.gm.gmall.common.auth.AuthUtils;
import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.feignClient.cart.CartFeignClient;
import com.gm.gmall.common.feignClient.product.ProductFeignClient;
import com.gm.gmall.common.feignClient.user.UserFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.cart.CartInfo;
import com.gm.gmall.model.user.UserAddress;
import com.gm.gmall.model.vo.order.OrderDataVo;
import com.gm.gmall.model.vo.order.OrderDetailVo;
import com.gm.gmall.model.vo.user.UserInfoId;
import com.gm.gmall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 获取订单信息
 * @author gym
 * @create 2022/9/13 0013 18:29
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    CartFeignClient cartFeignClient;
    @Autowired
    ProductFeignClient productFeignClient;
    @Autowired
    UserFeignClient userFeignClient;
    /**
     * 查询选中的商品信息
     * @return 0
     */
    @Override
    public OrderDataVo getData() {
        OrderDataVo orderDataVo = new OrderDataVo();
        //远程调用cart查询选中的商品信息
        Result<List<CartInfo>> check = cartFeignClient.getCheck();
        List<CartInfo> cartInfos = check.getData();
        List<OrderDetailVo> detailVos = cartInfos.stream().map(cartInfo -> {
            OrderDetailVo detailVo = new OrderDetailVo();
            detailVo.setImgUrl(cartInfo.getImgUrl());
            detailVo.setSkuName(cartInfo.getSkuName());
            detailVo.setSkuNm(cartInfo.getSkuNum());
            detailVo.setSkuId(Integer.parseInt(cartInfo.getSkuId().toString()));
            BigDecimal price= productFeignClient.getPrice(cartInfo.getSkuId()).getData();
            detailVo.setOrderPrice(price);
            return detailVo;
        }).collect(Collectors.toList());
        orderDataVo.setDetailArrayList(detailVos);
        //选中的商品数量
        Integer integer = detailVos.stream()
                .map(OrderDetailVo::getSkuNm)
                        .reduce(Integer::sum)
                .get();
        orderDataVo.setTotalNum(integer);
        //商品总价
        BigDecimal totalPrice = detailVos.stream().map(detailVo ->
                detailVo.getOrderPrice().multiply(new BigDecimal(detailVo.getSkuNm()))
        ).reduce(BigDecimal::add).get();
        orderDataVo.setTotalAmount(totalPrice);
        //地址
        Result<List<UserAddress>> list = userFeignClient.list();
        orderDataVo.setUserAddressList(list.getData());

        //生成订单号
        String tradeNo= getTradeNo();
        orderDataVo.setTradeNo(tradeNo);
        return orderDataVo;
    }

    public String getTradeNo() {
        long l = System.currentTimeMillis();
        UserInfoId info = AuthUtils.getInfo();
        String tradeNo=l+"_"+info.getUserId();
        //存入redis
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(RedisConstant.TRADE_NO+tradeNo,"哈哈",15, TimeUnit.SECONDS);
        return tradeNo;
    }
}
