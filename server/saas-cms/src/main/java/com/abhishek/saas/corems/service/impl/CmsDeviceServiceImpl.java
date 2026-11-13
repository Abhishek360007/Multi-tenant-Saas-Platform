package com.abhishek.saas.corems.service.impl;


import com.abhishek.saas.common.util.IdUtils;
import com.abhishek.saas.corems.entity.CmsDevice;
import com.abhishek.saas.corems.mapper.CmsDeviceMapper;
import com.abhishek.saas.corems.service.CmsDeviceService;
import com.abhishek.saas.tenant.base.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 设备
 *
 * @author WangChenChen
 * @version 2.0
 * @date 2023/10/25 10:40
 */


@Slf4j
@Service
@AllArgsConstructor
public class CmsDeviceServiceImpl extends BaseServiceImpl<CmsDeviceMapper, CmsDevice> implements CmsDeviceService {

    @Override
    public boolean save(CmsDevice entity) {
        entity.setDeviceId(IdUtils.getStandaloneId());
        return super.save(entity);
    }

}
