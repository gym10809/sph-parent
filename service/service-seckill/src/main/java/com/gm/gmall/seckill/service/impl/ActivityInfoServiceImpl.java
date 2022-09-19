package com.gm.gmall.seckill.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gm.gmall.model.activity.ActivityInfo;
import com.gm.gmall.seckill.mapper.ActivityInfoMapper;
import com.gm.gmall.seckill.service.ActivityInfoService;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【activity_info(活动表)】的数据库操作Service实现
* @createDate 2022-09-19 19:32:26
*/
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo>
    implements ActivityInfoService {

}




