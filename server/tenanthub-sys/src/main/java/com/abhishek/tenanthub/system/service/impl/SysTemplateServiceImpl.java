package com.abhishek.tenanthub.system.service.impl;

import cn.hutool.core.lang.tree.TreeNode;
import cn.hutool.core.lang.tree.TreeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.abhishek.tenanthub.common.enums.MenuTargetEnum;
import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.system.entity.SysMenu;
import com.abhishek.tenanthub.system.entity.SysTemplate;
import com.abhishek.tenanthub.system.entity.SysTemplateProxy;
import com.abhishek.tenanthub.system.mapper.SysTemplateMapper;
import com.abhishek.tenanthub.system.service.SysMenuService;
import com.abhishek.tenanthub.system.service.SysTemplateService;
import com.abhishek.tenanthub.system.vo.UpdateMenusVO;
import com.abhishek.tenanthub.tenant.base.service.impl.BaseServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


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
public class SysTemplateServiceImpl extends BaseServiceImpl<SysTemplateMapper, SysTemplate>
        implements SysTemplateService {

    private final SysMenuService menuService;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean removeById(Serializable id) {
        Long tid = Long.valueOf(id.toString());
        if (baseMapper.userReference(tid) > 0) {
            throw new BizException("This template is still used by tenants and cannot be deleted");
        }
        // Delete the assigned menus first
        baseMapper.deleteHaveMenus(tid);
        return super.removeById(tid);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean save(SysTemplate entity) {
        boolean result = super.save(entity);
        // updateMenus(entity.getId(), entity.getMenuIds());
        return result;
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateById(SysTemplate entity) {
        boolean result = super.updateById(entity);
        // updateMenus(entity.getId(), entity.getMenuIds());
        return result;
    }


    @Override
    public UpdateMenusVO listHaveMenus(Long templateId) {
        // Menus already assigned to the template ID
        var haveHashSet = baseMapper.selectByTemplateId(templateId);

        // Get non-platform menus
        var wrapper = new LambdaQueryWrapper<SysMenu>();
        wrapper.select(SysMenu::getMenuId, SysMenu::getParentId, SysMenu::getMenuName, SysMenu::getSort)
                .ne(SysMenu::getTarget, MenuTargetEnum.SYSTEM.getCode());

        var menus = menuService.list(wrapper)
                .stream().map(r -> new TreeNode<>(r.getMenuId(), r.getParentId(), r.getMenuName(), r.getSort()))
                .collect(Collectors.toList());

        var roots = TreeUtil.build(menus, 0L);
        return UpdateMenusVO.builder().have(haveHashSet).all(roots).build();
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateMenus(Long templateId, Set<Long> menus) {
        if (!exist(SysTemplate::getId, templateId)) {
            throw new BizException(String.format("Template ID %d does not exist", templateId));
        }
        baseMapper.deleteHaveMenus(templateId);
        if (menus != null && !menus.isEmpty()) {
            return baseMapper.insertByMenus(templateId, menus) > 0;
        }
        return true;
    }


    @Override
    public Map<String, Set<SysTemplate>> listByTenantIds(Set<String> tenantIds) {
        return baseMapper.selectListByTenantIds(tenantIds)
                .stream()
                .collect(Collectors.groupingBy(
                                SysTemplateProxy::getTenantId,
                                Collectors.mapping(r -> {
                                    return SysTemplate.builder()
                                            .id(r.getId())
                                            .name(r.getName())
                                            .description(r.getDescription())
                                            .build();
                                }, Collectors.toSet())
                        )
                );
    }


}
