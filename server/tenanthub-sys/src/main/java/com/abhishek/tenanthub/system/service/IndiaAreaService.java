package com.abhishek.tenanthub.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.system.entity.IndiaArea;

public interface IndiaAreaService extends IService<IndiaArea> {


    IPage<IndiaArea> pageKeywords(SearchPO params);


}
