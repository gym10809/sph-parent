package com.gm.gmall.model.to;

import com.gm.gmall.model.order.OrderDetail;
import com.gm.gmall.model.user.UserAddress;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author gym
 * @create 2022/9/20 0020 19:58
 */
@Data
public class SeckillOrderMsg {
    private List<UserAddress> userAddressListList;
    private BigDecimal totalAmount;
    private List<OrderDetail> detailArrayList;

}
