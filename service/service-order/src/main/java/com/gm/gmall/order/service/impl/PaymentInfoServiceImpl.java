package com.gm.gmall.order.service.impl;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.common.util.DateUtil;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.payment.PaymentInfo;
import com.gm.gmall.order.service.OrderInfoService;
import com.gm.gmall.order.service.PaymentInfoService;
import com.gm.gmall.order.mapper.PaymentInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
* @author Administrator
* @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
* @createDate 2022-09-11 21:49:03
*/
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService{

    @Autowired
    OrderInfoService orderInfoService;
    /**
     * 保存订单消息
     * @param map
     */
    @Override
    public PaymentInfo savePayment(Map<String, String> map) {
        //幂等性，判断是否被保存过
        PaymentInfo one = getOne(new LambdaQueryWrapper<PaymentInfo>()
                .eq(PaymentInfo::getOutTradeNo, map.get("out_trade_no"))
                .eq(PaymentInfo::getOrderId, Long.parseLong(map.get("out_trade_no").split("_")[1])));
        if (one!=null){
            //保存过直接返回
            return one;
        }
        PaymentInfo info=new PaymentInfo();
        info.setOutTradeNo(map.get("out_trade_no"));
        info.setUserId(Long.parseLong(map.get("out_trade_no").split("_")[1]));
        //获取用户信息
      OrderInfo orderInfo= orderInfoService.getByUserIdAndTrade(map.get("out_trade_no"),Long.parseLong(map.get("out_trade_no").split("_")[1]));
        info.setOrderId(orderInfo.getId());
        info.setPaymentType("支付宝");
        info.setTradeNo(map.get("trade_no"));
        info.setTotalAmount(new BigDecimal(map.get("total_amount")));
        info.setSubject(map.get("subject"));
        info.setPaymentStatus(map.get("trade_status"));
        info.setCreateTime(new Date());
        info.setCallbackTime(DateUtil.parseDate(map.get("notify_time"),"yyyy-MM-dd HH:mm:ss"));
        info.setCallbackContent(Jsons.toJson(map));
        //保存订单
        save(info);
        return info;
    }
}




