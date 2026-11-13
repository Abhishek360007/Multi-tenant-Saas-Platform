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
     * Query by keyword
     *
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    IPage<SysTenant> pageKeywords(SearchPO params);


    /**
     * Query by keyword
     *
     * @author Wang Chen Chen
     * @date 2021/8/25 9:41
     */
    IPage<SysTenant> simplePageKeywords(SearchPO params);


    Collection<TenantSimpleDataVO> simpleSearchQuery(SearchPO po);



    SysTenant getSimpleById(String id);


    /**
     * Create tenant
     *
     * @param params
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    TenantCreatedSuccessVO create(CreateTenantPO po) throws Exception;


    /**
     * Reset tenant data
     *
     * @param params
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    TenantCreatedSuccessVO resetData(String tenantId);


    /**
     * Update tenant permission templates
     *
     * @param tenantId
     * @param templateId
     * @return
     * @author Wang Chen Chen
     * @date 2021/7/16 17:31
     */
    boolean updateTemplate(String tenantId, Set<Long> templateIds);


}
