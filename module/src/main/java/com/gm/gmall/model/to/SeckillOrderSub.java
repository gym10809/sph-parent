package com.gm.gmall.model.to;

import com.gm.gmall.model.order.OrderDetail;
import com.gm.gmall.model.user.UserAddress;
import lombok.Data;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/20 0020 21:06
 */
@Data
public class SeckillOrderSub {
    private String consignee;
    private String consigneeTel;
    private String orderComment;
    private String deliveryAddress;
    private List<OrderDetail> detailList;
    private String paymentway;

}
