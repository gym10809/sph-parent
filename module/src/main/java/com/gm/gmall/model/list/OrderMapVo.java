package com.gm.gmall.model.list;

import lombok.Data;

/**
 * @author gym
 * @create 2022/9/5 0005 15:09
 */
@Data
public class OrderMapVo {

    private String type; // 1综合排序，2价格排序
    private String sort; // desc 降序  asc 升序
}
