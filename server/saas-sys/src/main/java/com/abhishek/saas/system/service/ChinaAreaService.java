package com.abhishek.saas.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.abhishek.saas.common.po.SearchPO;
import com.abhishek.saas.system.entity.ChinaArea;

public interface ChinaAreaService extends IService<ChinaArea> {


    IPage<ChinaArea> pageKeywords(SearchPO params);


}
