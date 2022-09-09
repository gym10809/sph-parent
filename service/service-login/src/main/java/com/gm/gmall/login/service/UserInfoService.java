package com.gm.gmall.login.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.user.UserInfo;
import com.gm.gmall.model.vo.LoginSuccessVo;

/**
* @author Administrator
* @description 针对表【user_info(用户表)】的数据库操作Service
* @createDate 2022-09-06 20:32:54
*/
public interface UserInfoService extends IService<UserInfo> {

    LoginSuccessVo login(UserInfo info);

    void logout(String token);
}
