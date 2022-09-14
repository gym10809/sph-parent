package com.gm.gmall.order.service.impl;

import com.gm.gmall.common.auth.AuthUtils;
import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.execption.GmallException;
import com.gm.gmall.common.feignClient.cart.CartFeignClient;
import com.gm.gmall.common.feignClient.product.ProductFeignClient;
import com.gm.gmall.common.feignClient.user.UserFeignClient;
import com.gm.gmall.common.feignClient.ware.WareFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.model.cart.CartInfo;
import com.gm.gmall.model.enums.OrderStatus;
import com.gm.gmall.model.enums.ProcessStatus;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.user.UserAddress;
import com.gm.gmall.model.vo.order.OrderDataVo;
import com.gm.gmall.model.vo.order.OrderDetailVo;
import com.gm.gmall.model.vo.order.OrderMsg;
import com.gm.gmall.model.vo.order.OrderSubmitVo;
import com.gm.gmall.model.vo.user.UserInfoId;
import com.gm.gmall.order.service.OrderInfoService;
import com.gm.gmall.order.service.OrderService;
import com.mchange.v1.identicator.IdList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    @Autowired
    WareFeignClient wareFeignClient;
    @Autowired
    OrderInfoService orderInfoService;
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
            detailVo.setSkuId(cartInfo.getSkuId());
            BigDecimal price= productFeignClient.getPrice(cartInfo.getSkuId()).getData();
            detailVo.setOrderPrice(price);
            String stock = wareFeignClient.hasStock(cartInfo.getSkuId(), cartInfo.getSkuNum());
            detailVo.setHasStock(stock);
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
        ops.set(RedisConstant.ORDER_TEMP_TOKEN+tradeNo,"1",15, TimeUnit.MINUTES);
        return tradeNo;
    }

    @Override
    public boolean checkToken(String tradeNo) {
        //lua脚本保证原子性,验证后就删除
        String lua="if redis.call(\"get\",KEYS[1]) == ARGV[1] then " +
                "    return redis.call(\"del\",KEYS[1]) " +
                "else " +
                "    return 0 " +
                "end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(lua, Long.class)
                , Arrays.asList(RedisConstant.ORDER_TEMP_TOKEN + tradeNo),
                "1");
        if (execute>0) {
            //大于0，代表校验成功
            return true;
        }
        return false;
    }

    /**
     * 提交订单
     * @param tradeNo
     * @param orderSubmitVo
     * @return
     */
    @Override
    public String submit(String tradeNo, OrderSubmitVo orderSubmitVo) {
        //检验令牌是否过期
        boolean b = checkToken(tradeNo);
        if (!b){
            //校验失败
            throw new GmallException(ResultCodeEnum.TOKEN_INVALID);
        }
        //校验库存
        List<OrderDetailVo> detailVos = orderSubmitVo.getOrderDetailList();
        List<String> notEnough=new ArrayList<>();
        detailVos.forEach(orderDetailVo -> {
            String s = wareFeignClient.hasStock(orderDetailVo.getSkuId(), orderDetailVo.getSkuNm());
            //不等于1则是不足，添加进去，方便后面抛出不足的商品信息
            if (!"1".equals(s)){
                notEnough.add(orderDetailVo.getSkuName());
            }
        });
        //判断是否存在不足的商品
        if (notEnough.size()>0){
            //把库存不足的商品名字都抛出去
            String s = notEnough.stream().reduce((i, j) -> i + "" + j).get();
            throw new GmallException(ResultCodeEnum.NUM_NOT_ENOUGH.getMessage()+s
                    ,ResultCodeEnum.NUM_NOT_ENOUGH.getCode());
        }
        //判断价格是否匹配
        List<String> misMatch =new ArrayList();
        detailVos.forEach(orderDetailVo -> {
            BigDecimal data = productFeignClient.getPrice(orderDetailVo.getSkuId()).getData();
            if (!data.equals(orderDetailVo.getOrderPrice())){
                //不等，则同上，添加信息至数组
                misMatch.add(orderDetailVo.getSkuName());
            }
        });
        if (misMatch.size()>0){
            String s = misMatch.stream().reduce((i, j) -> i + "" + j).get();
            throw new GmallException(ResultCodeEnum.MISMATCH.getMessage()+s
                    ,ResultCodeEnum.NUM_NOT_ENOUGH.getCode());
        }
        //保存订单信息
        String orderId= orderInfoService.saveOrder(tradeNo,orderSubmitVo);
        //、清除购物车中选中的商品
        cartFeignClient.deleteChecked();

//        //45min不支付就关闭。
//        ScheduledExecutorService pool = Executors.newScheduledThreadPool(10);
//        pool.schedule(()->{
//            closeOrder(Long.getLong(orderId));
//        },45,TimeUnit.MINUTES);


        return orderId;

    }

    @Override
    public void closeOrder(OrderMsg orderMsg) {
        ProcessStatus closed = ProcessStatus.CLOSED;
        //关闭订单，处于过期、已完结即可关单
        List<String> judge=new ArrayList<>();
        judge.add(ProcessStatus.FINISHED.name());
        judge.add(ProcessStatus.UNPAID.name());
        //利用cas，判断关闭订单
        orderInfoService.closeOrder(orderMsg,closed,judge);
    }
//    @Scheduled(cron = "0 */5 * * * ?")
//    public void closeOrder(Long orderId){
//
//    }
}

