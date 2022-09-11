package com.gm.gmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.payment.PaymentInfo;
import com.gm.gmall.order.service.PaymentInfoService;
import com.gm.gmall.order.mapper.PaymentInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【payment_info(支付信息表)】的数据库操作Service实现
* @createDate 2022-09-11 21:49:03
*/
@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo>
    implements PaymentInfoService{

}




