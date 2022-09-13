package com.gm.gmall.common.auth;


import com.gm.gmall.common.constant.RedisConstant;
import com.gm.gmall.model.vo.user.UserInfoId;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;

/**
 * @author gym
 * @create 2022/9/13 0013 20:01
 */
public class AuthUtils {

    public static UserInfoId getInfo(){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        UserInfoId info=new UserInfoId();
        String userId = request.getHeader(RedisConstant.USERID);
        if (!StringUtils.isEmpty(userId)){
            info.setUserId(Long.parseLong(userId));
        }
        String header = request.getHeader(RedisConstant.TEMPID);
        info.setUserTempId(header);
        return info;
    }
}
