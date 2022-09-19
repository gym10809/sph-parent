package com.gm.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author gym
 * @create 2022/9/18 0018 21:19
 */
@Data
public class WareMapItem {
    private Long wareId;
    private List<Long> skuIds;
}
