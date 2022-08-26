package com.gm.gmall.product.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gm.gmall.model.product.SpuInfo;

/**
* @author Administrator
* @description 针对表【spu_info(商品表)】的数据库操作Service
* @createDate 2022-08-23 15:32:33
*/
public interface SpuInfoService extends IService<SpuInfo> {

    void saveSpuInfo(SpuInfo spuInfo);
}
