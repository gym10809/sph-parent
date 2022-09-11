package com.gm.gmall.cart.service.impl;

import com.gm.gmall.cart.service.CartService;
import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.common.execption.GmallException;
import com.gm.gmall.common.feignClient.product.ProductFeignClient;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.model.cart.CartInfo;
import com.gm.gmall.model.product.SkuInfo;
import com.gm.gmall.model.vo.user.UserInfoId;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import springfox.documentation.spring.web.json.Json;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author gym
 * @create 2022/9/8 0008 18:58
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    ProductFeignClient feignClient;

    /**
     * 添加商品至购物车
     * @param skuId
     * @param skuNum
     * @return
     */
    @Override
    public SkuInfo addCart(Long skuId, Integer skuNum) {
        //获得老请求，并得到相关id
        UserInfoId userInfoId = getInfoId();
        //判定使用临时还是用户
        String cacheKey = getCacheKey(userInfoId);
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(cacheKey);
        if (userInfoId.getUserId()==null){
            //设置临时购物车的过期时间
            ops.expire(90, TimeUnit.DAYS);
        }
        //添加商品
        //判断此商品是否已添加过
        Boolean hasKey = ops.hasKey(skuId.toString());
        if (!hasKey){
            //不存在，新存储
            //判断数量是否超量
            Long size = ops.size();
            if (size+1>RedisConstant.MAX_SIZE){
                throw new GmallException(ResultCodeEnum.SIZE_OVER_FLOW);
            }
            SkuInfo skuInfo = saveData(skuId, skuNum, ops);
            return skuInfo;
        }else {
            //添加过
            SkuInfo skuInfo = upData(skuId, skuNum, ops);
            return skuInfo;
        }
    }

    /**
     * 得到里面的id，userid和tempId
     * @return
     */
    private UserInfoId getInfoId() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String header = request.getHeader(RedisConstant.USERID);
        UserInfoId userInfoId=new UserInfoId();
        if (!StringUtils.isEmpty(header)){
            userInfoId.setUserId(Long.parseLong(header));
        }
        String tempId = request.getHeader(RedisConstant.TEMPID);
        userInfoId.setUserTempId(tempId);
        return userInfoId;
    }

    /**
     * 通过id判定临时购物车还是用户购物车
     * @param userInfoId
     * @return
     */
    private String getCacheKey(UserInfoId userInfoId) {
        String cacheKey=RedisConstant.PRE_CART;
        if (userInfoId.getUserId()!=null){
            cacheKey += userInfoId.getUserId();
        }else {
            cacheKey += userInfoId.getUserTempId();
        }
        return cacheKey;
    }

    /**
     * 查询购物车
     * @return
     */
    @Override
    public List<CartInfo> cartLiat() {
        //查询购物车列表
        //判定是否是登陆情况
        UserInfoId infoId = getInfoId();
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(RedisConstant.PRE_CART + infoId.getUserTempId());
        List<CartInfo> cartInfos = getCartInfos(ops);
        //todo 更新价格
        if (infoId.getUserId()!=null){
            //用户购物车
            //绑定用户购物车
            BoundHashOperations<String, String, String> userOps= redisTemplate.boundHashOps(RedisConstant.PRE_CART + infoId.getUserId());
            //查看临时购物车是否有数据
            Long size = ops.size();
            if (size>0){
                //合并临时购物车到用户购物车中
                    for (CartInfo cartInfo : cartInfos) {
                        addTemp2User(cartInfo,infoId);
                      ops.delete(cartInfo.getSkuId().toString());
                    }
            }
            //查询用户购物车
            List<CartInfo> userInfos = getCartInfos(userOps);
            return userInfos;
        }else {
            //临时购物车
            return cartInfos;
        }
    }

    @Override
    public void addToCart(Long skuId, Integer num) {
        //判定是哪个cacheKey
        UserInfoId infoId = getInfoId();
        String cacheKey = getCacheKey(infoId);
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(cacheKey);
        String cartString = ops.get(skuId.toString());
        CartInfo cartInfo = Jsons.toObject(cartString, CartInfo.class);
        cartInfo.setSkuNum(cartInfo.getSkuNum()+num);
        //取出改变数量后，再放进去
        ops.put(skuId.toString(),Jsons.toJson(cartInfo));
    }

    @Override
    public void checkCart(Long skuId, Integer status) {
        UserInfoId infoId = getInfoId();
        String cacheKey = getCacheKey(infoId);
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(cacheKey);
        String cartString = ops.get(skuId.toString());
        CartInfo cartInfo = Jsons.toObject(cartString, CartInfo.class);
        cartInfo.setIsChecked(status);
        ops.put(skuId.toString(),Jsons.toJson(cartInfo));
    }

    @Override
    public void deleteCart(Long skuId) {
        UserInfoId infoId = getInfoId();
        String cacheKey = getCacheKey(infoId);
        BoundHashOperations<String, String, String> ops= redisTemplate.boundHashOps(cacheKey);
        ops.delete(skuId.toString());
    }

    private void addTemp2User(CartInfo cartInfo,UserInfoId infoId ) {
        BoundHashOperations<String, String, String> userOps= redisTemplate.boundHashOps(RedisConstant.PRE_CART +infoId.getUserId());
        if (userOps.size()+1<RedisConstant.MAX_SIZE){
            String c = userOps.get(cartInfo.getSkuId().toString());
            if (c!=null){
                CartInfo cartInfo1 = Jsons.toObject(c, CartInfo.class);
                cartInfo1.setSkuNum(cartInfo1.getSkuNum()+cartInfo.getSkuNum());
                userOps.put(cartInfo.getSkuId().toString(),Jsons.toJson(cartInfo1));
            }else {
                userOps.put(cartInfo.getSkuId().toString(),Jsons.toJson(cartInfo));
            }
        }else {
            throw new GmallException(ResultCodeEnum.SIZE_OVER_FLOW);
        }
    }

    /**
     * 获取购物列表
     * @param ops
     * @return
     */
    private List<CartInfo> getCartInfos(BoundHashOperations<String, String, String> ops) {
        List<String> values = ops.values();
        List<CartInfo> cartInfos = values.stream()
                .map(string -> Jsons.toObject(string, CartInfo.class))
                .sorted((o1, o2) -> o2.getCreateTime().compareTo(o1.getCreateTime())).collect(Collectors.toList());
        return cartInfos;
    }

    /**
     * 增加商品的个体数量
     * @param skuId
     * @param skuNum
     * @param ops
     * @return
     */
    private SkuInfo upData(Long skuId, Integer skuNum, BoundHashOperations<String, String, String> ops) {
        String s = ops.get(skuId.toString());
        Result<SkuInfo> skuInfo = feignClient.getSkuInfo(Integer.parseInt(skuId.toString()));
        //把得到的数据转回对象
        CartInfo cartInfo = Jsons.toObject(s, CartInfo.class);
        //判断是否超过单个物品叠加数量
        Integer skuNum1 = cartInfo.getSkuNum();
        if (skuNum1+1>RedisConstant.MAX_NUM){
            throw new GmallException(ResultCodeEnum.NUM_OUT_OF_RANGE);
        }
        //重新设置数量
        cartInfo.setSkuNum(cartInfo.getSkuNum()+ skuNum);
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(skuInfo.getData().getPrice());
        //存进redis

        ops.put(skuId.toString(),Jsons.toJson(cartInfo));
        //转为skuInfo
        SkuInfo skuInfo1=cartInfo2SkuInfo(cartInfo);
        return skuInfo1;
    }

    /**
     * 新增商品至购物车中
     * @param skuId
     * @param skuNum
     * @param ops
     * @return
     */
    private SkuInfo saveData(Long skuId, Integer skuNum, BoundHashOperations<String, String, String> ops) {
        Result<SkuInfo> data = feignClient.getSkuInfo(Integer.parseInt(skuId.toString()));
        SkuInfo skuInfo = data.getData();
        //把skuinfo转为存储需要的数据
        CartInfo cartInfo= skuInfo2CartInfo(skuInfo);
        cartInfo.setSkuNum(skuNum);
        //存储在redis中
        ops.put(skuId.toString(), Jsons.toJson(cartInfo));
        return skuInfo;
    }

    /**
     * 封装skuInfo
     * @param cartInfo
     * @return
     */
    private SkuInfo cartInfo2SkuInfo(CartInfo cartInfo) {
        SkuInfo skuInfo=new SkuInfo();
        skuInfo.setSkuName(cartInfo.getSkuName());
        skuInfo.setSkuDefaultImg(cartInfo.getImgUrl());
        skuInfo.setId(cartInfo.getSkuId());
        return skuInfo;
    }

    /**
     * 将的来的商品信息封装成购物车信息
     * @param skuInfo
     * @return
     */
    private CartInfo skuInfo2CartInfo(SkuInfo skuInfo) {
        CartInfo cartInfo=new CartInfo();
        cartInfo.setSkuId(skuInfo.getId());
        cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
        cartInfo.setSkuName(skuInfo.getSkuName());
        cartInfo.setIsChecked(1);
        cartInfo.setCreateTime(new Date());
        cartInfo.setUpdateTime(new Date());
        cartInfo.setSkuPrice(skuInfo.getPrice());
        cartInfo.setCartPrice(skuInfo.getPrice());
        return cartInfo;
    }
}
