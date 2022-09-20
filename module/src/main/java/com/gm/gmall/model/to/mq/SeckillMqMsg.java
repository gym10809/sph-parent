package com.gm.gmall.model.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author gym
 * @create 2022/9/20 0020 8:14
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillMqMsg {
    private Long skuId;
    private Long userId;
}
