package com.gm.gmall.pay.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author gym
 * @create 2022/9/16 0016 18:17
 */
@Configuration
public class AlipayClientAutoConfig {

    @Bean
    public AlipayClient alipayClient(AliPayProperties aliPayProperties){
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayProperties.getGatewayUrl(),
                aliPayProperties.getAppId(), aliPayProperties.getMerchantPrivateKey(),
                "json", aliPayProperties.getCharset(), aliPayProperties.getAlipayPublicKey(),
                aliPayProperties.getSignType());
        return alipayClient;
    }
}
