package com.gm.gmall.pay.service;

import com.alipay.api.AlipayApiException;

import java.util.Map;

/**
 * @author gym
 * @create 2022/9/16 0016 20:00
 */
public interface AliPayService {
    String getAliPAyHtml(Long orderId) throws AlipayApiException;

    boolean checkSing(Map<String, String> map) throws AlipayApiException;

    void sendMsg(Map<String, String> map);
}
