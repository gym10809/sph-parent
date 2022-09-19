package com.gm.gmall.model.to.mq;

import lombok.Data;

/**
 * @author gym
 * @create 2022/9/18 0018 20:13
 */
@Data
public class WareStockStatus {
    private Long orderId;
    private String status;
}
