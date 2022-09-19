package com.gm.gmall.model.vo.order;

import lombok.Data;

/**
 * @author gym
 * @create 2022/9/18 0018 21:01
 */
@Data
public class OrderSplitVo {
    private Long orderId;
    private String wareSkuMap;
}
