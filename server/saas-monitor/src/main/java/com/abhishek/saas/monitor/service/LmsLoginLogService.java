package com.abhishek.saas.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.abhishek.saas.common.po.SearchPO;
import com.abhishek.saas.monitor.entity.LmsLoginLog;


/**
 * <p>
 * 登录日志
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/6 15:28
 */

public interface LmsLoginLogService extends IService<LmsLoginLog> {

    IPage<LmsLoginLog> pageKeywords(SearchPO params);

}