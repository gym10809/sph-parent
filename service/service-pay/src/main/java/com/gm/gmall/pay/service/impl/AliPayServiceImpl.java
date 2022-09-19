package com.gm.gmall.pay.service.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayResponse;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.gm.gmall.common.execption.GmallException;
import com.gm.gmall.common.feignClient.order.OrderFeignClient;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.common.util.DateUtil;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.pay.config.AliPayProperties;
import com.gm.gmall.pay.service.AliPayService;
import com.gm.gmall.rabbit.consatant.RabbitConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Service;

import java.text.Format;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gym
 * @create 2022/9/16 0016 20:00
 */
@Service
public class AliPayServiceImpl implements AliPayService {
    @Autowired
    AlipayClient alipayClient;
    @Autowired
    OrderFeignClient orderFeignClient;
    @Autowired
    AliPayProperties aliPayProperties;
    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 对照sdk文档封装数据
     * @param orderId
     * @return
     * @throws AlipayApiException
     */
    @Override
    public String getAliPAyHtml(Long orderId) throws AlipayApiException {
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderId).getData();
        if (orderInfo.getExpireTime().before(new Date())) {
            throw new GmallException(ResultCodeEnum.TRADE_EXPIRE);
        }
        String s = DateUtil.format(orderInfo.getExpireTime());
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        Map<String,Object> map=new HashMap<>();
        map.put("out_trade_no",orderInfo.getOutTradeNo());
        map.put("total_amount",orderInfo.getTotalAmount().toString());
        map.put("subject","商品："+orderInfo.getOutTradeNo());
        map.put("product_code","FAST_INSTANT_TRADE_PAY");
        map.put("time_expire",s);

        String bizContent= Jsons.toJson(map);
        request.setBizContent(bizContent);
        request.setNotifyUrl(aliPayProperties.getNotifyUrl());
        request.setReturnUrl(aliPayProperties.getReturnUrl());
        AlipayResponse response = alipayClient.pageExecute(request);
        String result = response.getBody();
        return result;
    }

    @Override
    public boolean checkSing(Map<String, String> map) throws AlipayApiException {
        boolean b = AlipaySignature.rsaCheckV1(map,
                aliPayProperties.getAlipayPublicKey(),
                aliPayProperties.getCharset(),
                aliPayProperties.getSignType());

        return b;
    }

    /**
     * 支付成功，发送消息。修改订单状态
     * @param map
     */
    @Override
    public void sendMsg(Map<String, String> map) {
            rabbitTemplate.convertAndSend(RabbitConstant.ORDER_CHANGE,RabbitConstant.ORDER_PAYED_RK,Jsons.toJson(map));
    }
}
