package com.gm.gmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.gm.gmall.common.auth.AuthUtils;
import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.execption.GmallException;
import com.gm.gmall.common.feignClient.cart.CartFeignClient;
import com.gm.gmall.common.feignClient.product.ProductFeignClient;
import com.gm.gmall.common.feignClient.user.UserFeignClient;
import com.gm.gmall.common.feignClient.ware.WareFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.cart.CartInfo;
import com.gm.gmall.model.enums.OrderStatus;
import com.gm.gmall.model.enums.ProcessStatus;
import com.gm.gmall.model.order.OrderDetail;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.user.UserAddress;
import com.gm.gmall.model.vo.order.*;
import com.gm.gmall.model.vo.user.UserInfoId;
import com.gm.gmall.order.service.OrderDetailService;
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
import java.util.*;
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
    @Autowired
    OrderDetailService orderDetailService;
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

    /**
     * 根据仓库拆分订单
     * @param orderSplitVo
     * @return
     */
    @Override
    public List<WareChildOrderVo> splitOrder(OrderSplitVo orderSplitVo) {
        Long orderId = orderSplitVo.getOrderId();
        //获得总订单
        OrderInfo orderInfo = orderInfoService.getById(orderId);
        List<OrderDetail> details = orderDetailService.getOrderDetails(orderId,orderInfo.getUserId());
        orderInfo.setOrderDetailList(details);
        //根据仓库分类
        List<WareMapItem> items = Jsons.toObject(orderSplitVo.getWareSkuMap(), new TypeReference<List<WareMapItem>>(){});
        //拆分子订单
        List<OrderInfo> childOrders = items.stream().map(wareMapItem -> {
            OrderInfo child = getChild(orderInfo, wareMapItem);
            return child;
        }).collect(Collectors.toList());
        //修改父定单状态
        orderInfoService.changeStatus(orderId,orderInfo.getUserId(),ProcessStatus.SPLIT,Arrays.asList(ProcessStatus.PAID));
        //返回需要的数据
        List<WareChildOrderVo> wareChildOrderVoList = childOrders.stream().map(orderInfo1 -> {
            WareChildOrderVo childOrderVo = new WareChildOrderVo();
            childOrderVo.setOrderId(orderInfo1.getId());
            childOrderVo.setConsignee(orderInfo1.getConsignee());
            childOrderVo.setConsigneeTel(orderInfo1.getConsigneeTel());
            childOrderVo.setOrderComment(orderInfo1.getOrderComment());
            childOrderVo.setOrderBody(orderInfo1.getTradeBody());
            childOrderVo.setDeliveryAddress(orderInfo1.getDeliveryAddress());
            childOrderVo.setPaymentWay("2");
            childOrderVo.setWareId(orderInfo1.getWareId());
            List<WareChildOrderDetailItemVo> collect = orderInfo1.getOrderDetailList().stream().map(orderDetail -> {
                WareChildOrderDetailItemVo vo = new WareChildOrderDetailItemVo();
                vo.setSkuId(orderDetail.getSkuId());
                vo.setSkuNum(orderDetail.getSkuNum());
                vo.setSkuName(orderDetail.getSkuName());
                return vo;
            }).collect(Collectors.toList());
            childOrderVo.setDetails(collect);
            return childOrderVo;
        }).collect(Collectors.toList());
        return wareChildOrderVoList;
    }

    private OrderInfo getChild(OrderInfo orderInfo, WareMapItem wareMapItem) {
        //封装成子订单
        List<Long> skuIds = wareMapItem.getSkuIds();
        OrderInfo child = new OrderInfo();
        child.setConsignee(orderInfo.getConsignee());
        child.setConsigneeTel(orderInfo.getConsigneeTel());
        child.setParentOrderId(orderInfo.getId());
        //获取子订单的详细信息
        List<OrderDetail> childDetails = orderInfo.getOrderDetailList().stream().
                filter(orderDetail -> skuIds.contains(orderDetail.getSkuId()))
                .collect(Collectors.toList());
        BigDecimal totalAmount = childDetails.stream().
                map(detail -> detail.getOrderPrice().multiply(new BigDecimal(detail.getSkuNum()))).
                reduce(BigDecimal::add).get();
        child.setTotalAmount(totalAmount);
        child.setOrderStatus(orderInfo.getOrderStatus());
        child.setUserId(orderInfo.getUserId());
        child.setPaymentWay(orderInfo.getPaymentWay());
        child.setDeliveryAddress(orderInfo.getDeliveryAddress());
        child.setOrderComment(orderInfo.getOrderComment());
        child.setOutTradeNo(orderInfo.getOutTradeNo());
        child.setTradeBody(childDetails.get(0).getSkuName());
        child.setCreateTime(new Date());
        child.setExpireTime(orderInfo.getExpireTime());
        child.setProcessStatus(orderInfo.getProcessStatus());

        child.setTrackingNo("");
        child.setParentOrderId(orderInfo.getId());
        child.setImgUrl(childDetails.get(0).getImgUrl());
        child.setOrderDetailList(childDetails);
        child.setWareId(wareMapItem.getWareId().toString());
        child.setProvinceId(0L);
        child.setRefundableTime(orderInfo.getRefundableTime());
        child.setOperateTime(new Date());
        orderInfoService.save(child);
        childDetails.stream().forEach(childDetail->childDetail.setOrderId(orderInfo.getId()));
        orderDetailService.saveBatch(childDetails);
        return child;
    }
//    @Scheduled(cron = "0 */5 * * * ?")
//    public void closeOrder(Long orderId){
//
//    }
}

