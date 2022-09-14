package com.gm.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/14 0014 0:27
 */
@Data
public class OrderSubmitVo {
    private String consignee;
    private String consigneeTel;
    private String deliveryAddress;
    private String paymentWay;
    private String orderComment;
    private List<OrderDetailVo> orderDetailList;
}
