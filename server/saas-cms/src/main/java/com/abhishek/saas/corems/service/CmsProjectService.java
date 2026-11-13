package com.abhishek.saas.corems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.saas.common.po.SearchPO;
import com.abhishek.saas.corems.entity.CmsProject;
import com.abhishek.saas.corems.po.ProjectQueryPO;
import com.abhishek.saas.corems.vo.ResetPasswordVO;
import com.abhishek.saas.tenant.base.service.BaseService;


/**
 * <p>
 * 项目
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:33
 */

public interface CmsProjectService extends BaseService<CmsProject> {

    IPage<CmsProject> pageKeywords(ProjectQueryPO params);


    /**
     * 重置密码
     *
     * @param pwd
     * @return int
     * @date 2022/12/9 15:33
     */
    boolean resetPassword(ResetPasswordVO pwd);


    /**
     * 分页查询项目简单字段
     *
     * @param po
     * @author WangChenChen
     * @version 2.0
     * @date 2023/10/25 17:22
     */
    IPage<CmsProject> simplePageKeywords(SearchPO po);



}
