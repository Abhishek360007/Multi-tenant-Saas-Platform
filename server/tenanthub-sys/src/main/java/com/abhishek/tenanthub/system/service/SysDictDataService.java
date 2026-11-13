package com.abhishek.tenanthub.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.system.entity.SysDictData;
import com.abhishek.tenanthub.system.po.DictQueryPO;
import com.abhishek.tenanthub.system.vo.DictDataVO;
import com.abhishek.tenanthub.tenant.base.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:33
 */

public interface SysDictDataService extends BaseService<SysDictData> {


    IPage<SysDictData> pageKeywords(DictQueryPO params);


    /**
     * 根据关键字查询
     *
     * @author Wang Chen Chen
     * @date 2021/8/24 18:24
     */
    List<SysDictData> listByKey(String dictTypeKey);


    /**
     * 根据关键字查询
     *
     * @author Wang Chen Chen
     * @date 2021/8/24 18:24
     */
    Map<String, List<DictDataVO>> mapByKeys();

}
