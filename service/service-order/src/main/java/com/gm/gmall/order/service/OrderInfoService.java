package com.gm.gmall.order.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.enums.ProcessStatus;
import com.gm.gmall.model.order.OrderInfo;
import com.gm.gmall.model.vo.order.OrderMsg;
import com.gm.gmall.model.vo.order.OrderSubmitVo;

import java.util.List;

/**
* @author Administrator
* @description 针对表【order_info(订单表 订单表)】的数据库操作Service
* @createDate 2022-09-11 21:49:03
*/
public interface OrderInfoService extends IService<OrderInfo> {

    String saveOrder(String tradeNo, OrderSubmitVo orderSubmitVo);

    void closeOrder(OrderMsg orderMsg, ProcessStatus closed, List<String> judge);
}
