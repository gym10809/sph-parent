package com.gm.gmall.login.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.gm.gmall.login.service.UserAddressService;
import com.gm.gmall.login.mapper.UserAddressMapper;
import com.gm.gmall.model.user.UserAddress;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【user_address(用户地址表)】的数据库操作Service实现
* @createDate 2022-09-06 20:32:54
*/
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress>
    implements UserAddressService{

}




