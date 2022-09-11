package com.gm.gmall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gm.gmall.model.order.OrderStatusLog;
import com.gm.gmall.order.service.OrderStatusLogService;
import com.gm.gmall.order.mapper.OrderStatusLogMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【order_status_log】的数据库操作Service实现
* @createDate 2022-09-11 21:49:03
*/
@Service
public class OrderStatusLogServiceImpl extends ServiceImpl<OrderStatusLogMapper, OrderStatusLog>
    implements OrderStatusLogService{

}




