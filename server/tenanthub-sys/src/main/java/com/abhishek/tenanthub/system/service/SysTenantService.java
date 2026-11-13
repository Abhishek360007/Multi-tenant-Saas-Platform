package com.abhishek.tenanthub.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.system.entity.SysTenant;
import com.abhishek.tenanthub.system.po.CreateTenantPO;
import com.abhishek.tenanthub.system.po.TenantCreatedSuccessVO;
import com.abhishek.tenanthub.system.vo.TenantSimpleDataVO;
import com.abhishek.tenanthub.tenant.base.service.BaseService;

import java.util.Collection;
import java.util.Set;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:33
 */

public interface SysTenantService extends BaseService<SysTenant> {

    /**
     * 根据关键字查询
     *
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    IPage<SysTenant> pageKeywords(SearchPO params);


    /**
     * 根据关键字查询
     *
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    IPage<SysTenant> simplePageKeywords(SearchPO params);


    Collection<TenantSimpleDataVO> simpleSearchQuery(SearchPO po);



    SysTenant getSimpleById(String id);


    /**
     * 创建商户
     *
     * @param params
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    TenantCreatedSuccessVO create(CreateTenantPO po) throws Exception;


    /**
     * 商户 重置 数据
     *
     * @param params
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    boolean resetData(String tenantId);


    /**
     * 修改 租户权限模板
     *
     * @param tenantId
     * @param templateId
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    boolean updateTemplate(String tenantId, Set<Long> templateIds);


}
