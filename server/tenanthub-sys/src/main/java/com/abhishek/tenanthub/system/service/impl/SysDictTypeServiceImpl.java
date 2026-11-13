package com.abhishek.tenanthub.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.system.entity.SysDictData;
import com.abhishek.tenanthub.system.entity.SysDictType;
import com.abhishek.tenanthub.system.mapper.SysDictTypeMapper;
import com.abhishek.tenanthub.system.service.SysDictDataService;
import com.abhishek.tenanthub.system.service.SysDictTypeService;
import com.abhishek.tenanthub.tenant.base.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:36
 */


@Slf4j
@Service
@AllArgsConstructor
public class SysDictTypeServiceImpl extends BaseServiceImpl<SysDictTypeMapper, SysDictType>
        implements SysDictTypeService {

    private final SysDictDataService dictDataService;


    @Override
    public boolean save(SysDictType entity) {
        if (exist(SysDictType::getTypeKey, entity.getTypeKey())) {
            throw new BizException(String.format("Dictionary key %s already exists", entity.getTypeKey()));
        }
        return super.save(entity);
    }


    @Override
    public boolean updateById(SysDictType entity) {
        var wrapper = new LambdaQueryWrapper<SysDictType>()
                .eq(SysDictType::getTypeKey, entity.getTypeKey())
                .notIn(SysDictType::getTypeId, entity.getTypeId());
        if (super.count(wrapper) > 0) {
            throw new BizException(String.format("Dictionary key %s already exists", entity.getTypeKey()));
        }
        return super.updateById(entity);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        SysDictType dictType = getById(id);
        if (dictType == null) {
            throw new BizException(String.format("Dictionary type ID %s does not exist", id));
        }
        dictDataService.remove(new LambdaQueryWrapper<SysDictData>()
                .eq(SysDictData::getTypeKey, dictType.getTypeKey()));
        return super.removeById(id);
    }


}
