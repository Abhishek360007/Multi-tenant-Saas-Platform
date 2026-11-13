package com.abhishek.tenanthub.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.system.entity.IndiaArea;
import com.abhishek.tenanthub.system.mapper.IndiaAreaMapper;
import com.abhishek.tenanthub.system.service.IndiaAreaService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 * </p>
 *
 * @author WangChenChen
 * @version 1.1
 * @date 2022/12/9 15:29
 */

@Slf4j
@Service
@AllArgsConstructor
public class IndiaAreaServiceImpl extends ServiceImpl<IndiaAreaMapper, IndiaArea>
        implements IndiaAreaService {


    @Override
    public IPage<IndiaArea> pageKeywords(SearchPO params) {
        var wrapper = new LambdaQueryWrapper<IndiaArea>();
        Page<IndiaArea> page = Page.of(params.getPageIndex(), params.getPageSize());
        if (StringUtils.isNotBlank(params.getKeywords())) {
            wrapper.like(IndiaArea::getName, params.getKeywords())
                    .or()
                    .like(IndiaArea::getMergerName, params.getKeywords());
        }
        return baseMapper.selectPage(page, wrapper);
    }

    
}
