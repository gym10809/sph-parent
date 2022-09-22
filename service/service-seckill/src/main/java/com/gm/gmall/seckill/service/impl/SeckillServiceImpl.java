package com.gm.gmall.seckill.service.impl;

import java.math.BigDecimal;

import com.gm.gmall.common.feignClient.order.OrderFeignClient;
import com.gm.gmall.common.feignClient.user.UserFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.model.enums.OrderStatus;
import com.gm.gmall.model.enums.ProcessStatus;
import com.gm.gmall.model.order.OrderDetail;
import com.gm.gmall.model.to.SeckillOrderMsg;
import com.gm.gmall.model.to.SeckillOrderSub;
import com.gm.gmall.model.user.UserAddress;
import com.google.common.collect.Lists;
import com.gm.gmall.model.activity.CouponInfo;

import com.gm.gmall.common.auth.AuthUtils;
import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.execption.GmallException;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.common.util.DateUtil;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.common.util.MD5;
import com.gm.gmall.model.activity.SeckillGoods;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.to.mq.SeckillMqMsg;
import com.gm.gmall.model.vo.user.UserInfoId;
import com.gm.gmall.rabbit.consatant.RabbitConstant;
import com.gm.gmall.seckill.service.CacheService;
import com.gm.gmall.seckill.service.SeckillService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author gym
 * @create 2022/9/19 0019 20:33
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CacheService cacheService;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 获取秒杀码
     *
     * @param skuId
     * @return
     */
    @Override
    public String getSeckillSkuIdStr(Long skuId) {
        //使用userId、skuId、当天时间生成一个每个用户的每件商品的唯一秒杀码
        //判断当前商品是否是秒杀商品
        SeckillGoods goods = cacheService.getSeckillOrder(skuId);
        if (goods == null) {
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }
        //是否能开始秒杀
        Date time = new Date();
        if (time.before(goods.getStartTime())) {
            //秒杀还么开始
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START);
        }
        if (time.after(goods.getEndTime())) {
            //秒杀已经结束
            throw new GmallException(ResultCodeEnum.SECKILL_END);
        }
        //是否还有库存
        if (goods.getStockCount() <= 0) {
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH);
        }
        UserInfoId info = AuthUtils.getInfo();
        String date = DateUtil.formatDate(new Date());
        String skuIdStr = "" + skuId + info.getUserId() + date;
        //存在redis中
        redisTemplate.opsForValue()
                .setIfAbsent(RedisConstant.SECKILL_CODE + MD5.encrypt(skuIdStr),
                        "1", 1, TimeUnit.DAYS);
        return MD5.encrypt(skuIdStr);
    }

    /**
     * 开始进行秒杀
     *
     * @param skuId
     * @param skuIdStr
     * @return
     */
    @Override
    public ResultCodeEnum seckillOrder(Long skuId, String skuIdStr) {

        //检查秒杀码
        UserInfoId info = AuthUtils.getInfo();
        String str = MD5.encrypt("" + skuId + info.getUserId() + DateUtil.formatDate(new Date()));
        if (!(str.equals(skuIdStr) || !redisTemplate.hasKey(RedisConstant.SECKILL_GOODS + skuIdStr))) {
            return ResultCodeEnum.SECKILL_ILLEGAL;
        }
        //判断当前是否已经秒杀过了
        Long increment = redisTemplate.opsForValue().increment(RedisConstant.SECKILL_CODE + skuIdStr);
        if (increment > 1) {
            return ResultCodeEnum.SUCCESS;
        }
        //再查询一次当前商品
        SeckillGoods goods = cacheService.getSeckillOrder(skuId);
        if (goods == null) {
            return ResultCodeEnum.SECKILL_FINISH;
        }
        //查询秒杀时间
        Date date = new Date();
        if (date.before(goods.getStartTime())) {
            //秒杀还么开始
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START);
        }
        if (date.after(goods.getEndTime())) {
            //秒杀已经结束
            throw new GmallException(ResultCodeEnum.SECKILL_END);
        }
        //缓存商品预减库存
        if (goods.getStockCount() <= 0) {
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH);
        }


        //开始进行秒杀,先判断缓存的库存
        Long decrement = redisTemplate.opsForValue().decrement(RedisConstant.SECKILL_GOODS_STOCK + goods.getSkuId());
        if (decrement > 0) {
            //同步商品里的库存数据和单独存的库存数据
            goods.setStockCount(goods.getStockCount() - 1);
            //redis扣减成功，发消息给数据库进行最终秒杀
            //保存信息初始化订单消息至redis，方便后续据此更新状态码
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setTotalAmount(goods.getCostPrice());
            orderInfo.setUserId(info.getUserId());
            orderInfo.setImgUrl(goods.getSkuDefaultImg());
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setSkuId(skuId);
            orderDetail.setSkuName(goods.getSkuName());
            orderDetail.setImgUrl(goods.getSkuDefaultImg());
            orderDetail.setOrderPrice(goods.getPrice());
            orderDetail.setSkuNum(goods.getNum());
            orderDetail.setCreateTime(goods.getCreateTime());
            orderDetail.setUserId(info.getUserId());
            List<OrderDetail> orderDetails = Arrays.asList(orderDetail);
            orderInfo.setOrderDetailList(orderDetails);
            //保存初始化订单
            redisTemplate.opsForValue().set(RedisConstant.SECKILL_ORDER + str, Jsons.toJson(orderInfo), 1, TimeUnit.DAYS);

            rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_SECKILL,
                    RabbitConstant.SECKILL_DEC_RK, Jsons.toJson(new SeckillMqMsg(skuId, info.getUserId(), skuIdStr)));
            return ResultCodeEnum.SUCCESS;
        } else {
            return ResultCodeEnum.SECKILL_FINISH;
        }
    }

    /**
     * 检查订单状态
     *
     * @param skuId
     * @return
     */
    @Override
    public ResultCodeEnum checkOrder(Long skuId) {
        //判断状态
        //获取内存中的数据，判断是否存在
        String str = MD5.encrypt("" + skuId + AuthUtils.getInfo().getUserId() + DateUtil.formatDate(new Date()));
        String json = redisTemplate.opsForValue().get(RedisConstant.SECKILL_ORDER + str);
        if (json.isEmpty()) {
            //如果数据不为空或者不为X
            return ResultCodeEnum.SECKILL_RUN;
        }
        if ("X".equals(json)) {
            return ResultCodeEnum.SECKILL_FINISH;
        }
        OrderInfo orderInfo = Jsons.toObject(json, OrderInfo.class);
        //是否秒杀中
        if (orderInfo.getId() != null) {
            //秒杀成功
            return ResultCodeEnum.SECKILL_ORDER_SUCCESS;
        }
        //判断是否枪单成功
        if (orderInfo.getOperateTime() != null) {
            //抢单成功
            return ResultCodeEnum.SECKILL_SUCCESS;
        }
        return ResultCodeEnum.SUCCESS;
    }
    @Autowired
    UserFeignClient userFeignClient;
    @Override
    public SeckillOrderMsg getMsg(Long skuId) {
        SeckillOrderMsg msg = new SeckillOrderMsg();
        List<UserAddress> data = userFeignClient.list().getData();
        msg.setUserAddressListList(data);
        UserInfoId info = AuthUtils.getInfo();
        //得到秒杀码，从而得到秒杀商品
        String skuIdStr=MD5.encrypt(""+skuId+info.getUserId()+DateUtil.formatDate(new Date()));
        //获得缓存中的此商品
        String json = redisTemplate.opsForValue().get(RedisConstant.SECKILL_ORDER + skuIdStr);
        OrderInfo orderInfo = Jsons.toObject(json, OrderInfo.class);
        msg.setTotalAmount(orderInfo.getTotalAmount());
        msg.setDetailArrayList(orderInfo.getOrderDetailList());
        return msg;
    }
    @Autowired
    OrderFeignClient orderFeignClient;
    @Override
    public Long submit(OrderInfo orderInfo) {
        //从redis查询相关数据
        String str=MD5.encrypt(""+AuthUtils.getInfo().getUserId()+orderInfo.getOrderDetailList().get(0).getSkuId());
        String s = redisTemplate.opsForValue().get(RedisConstant.SECKILL_ORDER + str);
        OrderInfo info=Jsons.toObject(s,OrderInfo.class);
        info.setConsignee(orderInfo.getConsignee());
        info.setConsigneeTel(orderInfo.getConsigneeTel());
        //保存订单
    Result<Long> id=  orderFeignClient.submit(orderInfo);
        return id.getData();
    }
}
