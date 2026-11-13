package com.abhishek.tenanthub.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.monitor.entity.LmsOperLog;

/**
 * <p>
 * 操作日志
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2023/2/6 15:28
 */

public interface LmsOperLogService extends IService<LmsOperLog> {

    IPage<LmsOperLog> pageKeywords(SearchPO params);

}
