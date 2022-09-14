package com.gm.gmall.model.vo.order;

import com.gm.gmall.model.user.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author gym
 * @create 2022/9/13 0013 13:46
 */
@Data
public class OrderDataVo {
    private List<OrderDetailVo> detailArrayList;//每个商品的信息
    private Integer totalNum;//购买物品总数
    private BigDecimal totalAmount;//购买总价
    private List<UserAddress> userAddressList;//地址
    private String tradeNo;//订单号

}
