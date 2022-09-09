package com.gm.gmall.filter;

import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.user.UserInfo;
import com.gm.gmall.properties.UrlProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Mono;
import springfox.documentation.spring.web.json.Json;

import java.nio.charset.StandardCharsets;

/**
 * @author gym
 * @create 2022/9/6 0006 22:40
 */
@Component
public class GlobalUserFilter implements GlobalFilter {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    UrlProperties properties;
    @Bean
    public GlobalFilter userFilter() {
        return new GlobalUserFilter();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
        AntPathMatcher matcher=new AntPathMatcher();
        //判断是否是拒接访问url
        for (String url : properties.getRejectUrl()) {
            boolean match = matcher.match(url, path);
            if (match){
                //如果是拒接访问的路径，直接返回错误界面
                Result<String> result=Result.build("错误页面", ResultCodeEnum.PERMISSION);
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);
                String s = Jsons.toJson(result);
                DataBuffer wrap = response.bufferFactory().wrap(s.getBytes(StandardCharsets.UTF_8));
                response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
                return  response.writeWith(Mono.just(wrap));
            }
        }
        //直接放行不需要验证的资源
        for (String url : properties.getNoAuthUrl()) {
            boolean match = matcher.match(url, path);
            if (match){
                return chain.filter(exchange);
            }
        }

        //验证是否需要登录
        String location= properties.getLoginPage()+"?originUrl="+exchange.getRequest().getURI();
        for (String url : properties.getLoginUrl()) {
            boolean match = matcher.match(url, path);
            if (match){
                //获取token
                String token = getToken(request);
                //判断token是否存在
                if (!StringUtils.isEmpty(token)){
                    //存在token,获取info
                    String info = redisTemplate.opsForValue().get(RedisConstant.LOGIN_USER + token);
                    if (StringUtils.isEmpty(info)){
                        //说明此token有问题，返回登录界面
                        ServerHttpResponse response = getServerHttpResponse(location,exchange);
                        return response.setComplete();
                    }else {
                        //设置id
                        //自身的request自读，不允许修改
                        exchange = getServerWebExchange(exchange, info);
                        return chain.filter(exchange);
                    }
                }else {
                    //跳转至登录界面
                    ServerHttpResponse response = getServerHttpResponse(location,exchange);
                    return response.setComplete();
                }

            }
        }
        //到此为普通请求
        String token = getToken(request);
        String info = redisTemplate.opsForValue().get(RedisConstant.LOGIN_USER + token);
        if (token!=null && StringUtils.isEmpty(info)){
            //跳转至登录界面
            ServerHttpResponse response = getServerHttpResponse(location,exchange);
            return response.setComplete();
        }
        exchange = getServerWebExchange(exchange, info);
        return chain.filter(exchange);
    }

    private String getToken(ServerHttpRequest request) {
        String token ="";
        token= request.getHeaders().getFirst("token");
        if (StringUtils.isEmpty(token)){
            //查询cookie里的token
            HttpCookie first = request.getCookies().getFirst("token");
            if (first!=null){
                token = first.getValue();
            }
        }
        return token;
    }

    private ServerWebExchange getServerWebExchange(ServerWebExchange exchange, String info) {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst(RedisConstant.TEMPID);
        ServerHttpRequest.Builder builder = exchange.getRequest().mutate();
        if (cookie!=null){
            String userTempId = cookie.getValue();
            builder.header("userTempId",userTempId);
        }
        if (info!=null) {
            UserInfo userInfo = Jsons.toObject(info, UserInfo.class);
            builder.header("userId", userInfo.getId().toString());
        }
        ServerWebExchange webExchange = exchange.mutate().request(builder.build()).response(exchange.getResponse()).build();
        return webExchange;
    }

    private ServerHttpResponse getServerHttpResponse(String location,ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().set(HttpHeaders.LOCATION,location);
        //清除之前的token
        ResponseCookie token = ResponseCookie.from("token", "").path("/").domain(".gmall.com").maxAge(0).build();
        response.getCookies().set("token",token);
        return response;
    }
}
