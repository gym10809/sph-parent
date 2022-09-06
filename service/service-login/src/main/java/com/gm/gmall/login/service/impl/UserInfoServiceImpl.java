package com.gm.gmall.login.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gm.gmall.common.constant.RedisConst;
import com.gm.gmall.common.result.Result;
import com.gm.gmall.common.result.ResultCodeEnum;
import com.gm.gmall.common.util.Jsons;
import com.gm.gmall.common.util.MD5;
import com.gm.gmall.login.service.UserInfoService;
import com.gm.gmall.login.mapper.UserInfoMapper;
import com.gm.gmall.model.user.UserInfo;
import com.gm.gmall.model.vo.LoginSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import springfox.documentation.spring.web.json.Json;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Service实现
* @createDate 2022-09-06 20:32:54
*/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo>
    implements UserInfoService{

    @Autowired
    RedisTemplate redisTemplate;
    @Override
    public LoginSuccessVo login(String loginName, String passwd) {
        LambdaQueryWrapper<UserInfo> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(UserInfo::getLoginName,loginName).eq(UserInfo::getPasswd, MD5.encrypt(passwd));
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        LoginSuccessVo vo=new LoginSuccessVo();
        if (userInfo!=null){
            String token = UUID.randomUUID().toString().replace("-", "");
            vo.setToken(token);
            vo.setNickName(userInfo.getNickName());
            //存缓存
            redisTemplate.opsForValue().set(RedisConst.LOGIN_USER+token, Jsons.toJson(userInfo),7, TimeUnit.DAYS);

        }
        return vo;
    }
}




