package com.gm.gmall.order.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.order.OrderDetail;
import com.gm.gmall.order.service.OrderDetailService;
import com.gm.gmall.order.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2022-09-11 21:49:03
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




