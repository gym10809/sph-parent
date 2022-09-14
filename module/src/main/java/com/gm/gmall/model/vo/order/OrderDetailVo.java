package com.gm.gmall.model.vo.order;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author gym
 * @create 2022/9/13 0013 13:47
 */
@Data
public class OrderDetailVo {
    private String imgUrl;
    private String skuName;
    private Integer skuNm;
    private BigDecimal orderPrice;
    private Long skuId;

    private String hasStock = "1";
}
