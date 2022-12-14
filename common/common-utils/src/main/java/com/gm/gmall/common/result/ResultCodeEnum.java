package com.gm.gmall.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 *
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200,"成功"),
    FAIL(201, "失败"),
    SERVICE_ERROR(2012, "服务异常"),

    PAY_RUN(205, "支付中"),

    SIZE_OVER_FLOW(1345,"超出购物车数量限制"),
    NUM_OUT_OF_RANGE(1346,"超过单个商品的数量上限"),

    LOGIN_ERRO(2081, "登录失败"),
    LOGIN_AUTH(208, "未登陆"),
    PERMISSION(209, "没有权限"),
    SECKILL_NO_START(210, "秒杀还没开始"),
    SECKILL_RUN(211, "正在排队中"),
    SECKILL_NO_PAY_ORDER(212, "您有未支付的订单"),
    SECKILL_FINISH(213, "已售罄"),
    SECKILL_END(214, "秒杀已结束"),
    SECKILL_SUCCESS(215, "抢单成功"),
    SECKILL_FAIL(216, "抢单失败"),
    SECKILL_ILLEGAL(217, "请求不合法"),
    SECKILL_ORDER_SUCCESS(218, "下单成功"),
    COUPON_GET(220, "优惠券已经领取"),
    COUPON_LIMIT_GET(221, "优惠券已发放完毕"),
    TOKEN_INVALID(3001,"订单创建失效，请重新创建" ),
    NUM_NOT_ENOUGH(3002, "商品库存不足"),
    MISMATCH(3003,"价格不匹配"),
    TRADE_EXPIRE(3004,"订单已过期" );
    private Integer code;

    private String message;

    private ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
