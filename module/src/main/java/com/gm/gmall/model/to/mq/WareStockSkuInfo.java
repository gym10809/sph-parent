package com.gm.gmall.model.to.mq;

import lombok.Data;

/**
 * @author gym
 * @create 2022/9/18 0018 15:54
 */
@Data
public class WareStockSkuInfo {
    private Long skuId;
    private Integer skuNum;
    private String skuName;
}
