package com.gm.gmall.order.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gm.gmall.model.order.OrderInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author Administrator
* @description 针对表【order_info(订单表 订单表)】的数据库操作Mapper
* @createDate 2022-09-11 21:49:03
* @Entity com.gm.gmall.order.domain.OrderInfo
*/
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    void upDateOrderStatus(@Param("orderId") Long orderId,@Param("userId")Long userId, @Param("status") String status,@Param("judge") List<String> judge);
}




