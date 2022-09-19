package com.gm.gmall.model.to.mq;

import lombok.Data;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/18 0018 15:53
 */
@Data
public class WareStockMsg {
    private Long orderId;
    private String consignee;
   private String consigneeTel;
   private String orderComment;
   private String orderBody;
   private String deliveryAddress;
    private String paymentWay = "2";
    private List<WareStockSkuInfo> details;
}
