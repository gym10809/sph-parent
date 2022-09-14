package com.gm.gmall.order.service.impl;
import java.math.BigDecimal;

import com.gm.gmall.common.auth.AuthUtils;
import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.enums.OrderStatus;
import com.gm.gmall.model.enums.ProcessStatus;
import com.gm.gmall.model.order.OrderDetail;
import com.gm.gmall.model.vo.order.OrderDetailVo;
import com.gm.gmall.model.vo.order.OrderMsg;
import com.gm.gmall.rabbit.consatant.RabbitConstant;
import com.google.common.collect.Lists;
import com.gm.gmall.model.activity.CouponInfo;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.vo.order.OrderSubmitVo;
import com.gm.gmall.order.service.OrderDetailService;
import com.gm.gmall.order.service.OrderInfoService;
import com.gm.gmall.order.mapper.OrderInfoMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
* @createDate 2022-09-11 21:49:03
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{

    @Autowired
    OrderDetailService orderDetailService;
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Autowired
    RabbitTemplate rabbitTemplate;
    @Override
    public String saveOrder(String tradeNo, OrderSubmitVo orderSubmitVo) {
        //准备OrderInfo数据
        OrderInfo orderInfo=new OrderInfo();
        orderInfo.setConsignee(orderSubmitVo.getConsignee());
        orderInfo.setConsigneeTel(orderSubmitVo.getConsigneeTel());
        //计算总金额
        List<OrderDetailVo> detailVos = orderSubmitVo.getOrderDetailList();
        BigDecimal totalPrice = detailVos.stream().map(detailVo ->
                detailVo.getOrderPrice().multiply(new BigDecimal(detailVo.getSkuNm()))
        ).reduce(BigDecimal::add).get();
        orderInfo.setTotalAmount(totalPrice);
        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());
        orderInfo.setUserId(AuthUtils.getInfo().getUserId());
        orderInfo.setPaymentWay(orderSubmitVo.getPaymentWay());
        orderInfo.setDeliveryAddress(orderSubmitVo.getDeliveryAddress());
        orderInfo.setOrderComment(orderSubmitVo.getOrderComment());
        orderInfo.setOutTradeNo(tradeNo);
        //交易体，选第一个商品的名字
        orderInfo.setTradeBody(orderSubmitVo.getOrderDetailList().get(0).getSkuName());
        orderInfo.setCreateTime(new Date());
        //过期时间，30分钟后失效
        orderInfo.setExpireTime(new Date(System.currentTimeMillis()+RedisConstant.ORDER_EXPIRE));
        //订单的处理状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        //物流编号
        orderInfo.setTrackingNo("");
        orderInfo.setParentOrderId(0L);
        orderInfo.setImgUrl(orderSubmitVo.getOrderDetailList().get(0).getImgUrl());
        orderInfo.setProvinceId(0L);
        //真实支付金额
        orderInfo.setOriginalTotalAmount(totalPrice);
        orderInfo.setRefundableTime(new Date());
        orderInfo.setFeightFee(new BigDecimal("0"));

        orderInfoMapper.insert(orderInfo);
        //保存订单里面的商品的信息
        List<OrderDetail> orderDetails = orderSubmitVo.getOrderDetailList().stream().map(orderDetailVo -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(orderInfo.getId());
            orderDetail.setSkuId(orderDetailVo.getSkuId());
            orderDetail.setSkuName(orderDetailVo.getSkuName());
            orderDetail.setImgUrl(orderDetailVo.getImgUrl());
            orderDetail.setOrderPrice(orderDetailVo.getOrderPrice());
            orderDetail.setSkuNum(orderDetailVo.getSkuNm());
            orderDetail.setHasStock(orderDetailVo.getHasStock());
            orderDetail.setCreateTime(new Date());
            orderDetail.setSplitTotalAmount(orderDetailVo.getOrderPrice().multiply(new BigDecimal(orderDetailVo.getSkuNm())));
            orderDetail.setSplitActivityAmount(new BigDecimal("0"));
            orderDetail.setSplitCouponAmount(new BigDecimal("0"));
            orderDetail.setUserId(AuthUtils.getInfo().getUserId());
            return orderDetail;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetails);
        //发送消息至消息队列，添加消息至延迟队列
        OrderMsg orderMsg=new OrderMsg();
        orderMsg.setOrderId(orderInfo.getId());
        orderMsg.setUserId(orderInfo.getUserId());
        rabbitTemplate.convertAndSend(RabbitConstant.ORDER_CHANGE
                                        ,RabbitConstant.ORDER_CREATED_RK,
                                        Jsons.toJson(orderMsg));


        return orderInfo.getId().toString();
    }

    @Override
    public void closeOrder(OrderMsg orderMsg, ProcessStatus closed, List<String> judge) {
        String status=closed.name();
        //更改订单的状态
        orderInfoMapper.upDateOrderStatus(orderMsg.getOrderId(),orderMsg.getUserId(),status,judge);
    }
}




