package com.gm.gmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.order.service.OrderInfoService;
import com.gm.gmall.order.mapper.OrderInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service实现
* @createDate 2022-09-11 21:49:03
*/
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{

}




