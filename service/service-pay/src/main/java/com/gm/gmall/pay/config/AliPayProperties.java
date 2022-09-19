package com.gm.gmall.pay.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author gym
 * @create 2022/9/16 0016 18:10
 */
@Data
@Component
@ConfigurationProperties("ali.pay")
public class AliPayProperties {
    private String appId ;//应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    private String merchantPrivateKey;//商户私钥
    private String alipayPublicKey;//支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    private String notifyUrl;//服务器异步通知页面路径
    private String returnUrl;//页面跳转同步通知页面路径
    private String signType;//签名方式
    private String charset;//字符编码格式
    private String gatewayUrl;//支付宝网关
}
