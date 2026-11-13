package com.abhishek.tenanthub.system.controller;

import cn.hutool.core.lang.tree.Tree;
import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.system.entity.SysMenu;
import com.abhishek.tenanthub.system.service.SysMenuService;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

/**
 * <p>
 * Menu controller
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/8 10:15
 */

@Slf4j
@Tag(name = "[ Permissions ] Menus")
@RestController
@RequestMapping("/sys/menu")
@AllArgsConstructor
public class SysMenuController {

    private SysMenuService baseService;

    @Operation(summary = "Get one", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<SysMenu> getById(@PathVariable("id") Long id) {
        log.debug("get ID : {}", id);
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<SysMenu>> pageQuery(SearchPO params) {
        var page = baseService.pageKeywords(
                params, List.of(SysMenu::getMenuName, SysMenu::getPerms)
        );
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @Operation(summary = "Query all", description = "Query all")
    @GetMapping("/list")
    public JsonResult<List<SysMenu>> list() {
        List<SysMenu> list = baseService.list();
        list.sort(Comparator.comparing(SysMenu::getSort));
        return JsonResult.success(list);
    }


    @Operation(summary = "Query tree nodes", description = "Query all [as a tree]")
    @GetMapping("/tree")
    public JsonResult<List<Tree<Long>>> tree() {
        return JsonResult.success(baseService.tree());
    }


    /**
     * Depending on the current user type
     * Tenant users: tenant menus and common menus
     * Platform users: platform menus and common menus
     * <p>
     * Based on the menu target property
     */
    @Operation(summary = "Available menus", description = "All menus available to the current user in this tenant [as a tree]")
    @GetMapping("/available")
    public JsonResult<List<Tree<Long>>> available() {
        return JsonResult.success(
                baseService.treeCurrentUserAvailable()
        );
    }


    @Operation(summary = "Tenant menus", description = "All menus available to the tenant [as a tree]")
    @GetMapping("/tenant/available")
    public JsonResult<List<Tree<Long>>> tenantAvailable() {
        return JsonResult.success(
                baseService.treeTenantAvailable()
        );
    }


    @NoRepeatSubmit
    @Operation(summary = "Add", description = "Do not include id")
    @PostMapping()
    public JsonResult<SysMenu> save(@RequestBody @Validated(ValidCreate.class) SysMenu entity) {
        baseService.save(entity);
        return JsonResult.success(entity);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated(ValidUpdate.class) SysMenu entity) {
        boolean byId = baseService.updateById(entity);
        return JsonResult.success(byId);
    }


    @NoRepeatSubmit
    @Operation(summary = "Delete", description = "Only the ID is required")
    @DeleteMapping("/{id}")
    public JsonResult<Boolean> removeById(@PathVariable("id") Long id) {
        boolean byId = baseService.removeById(id);
        return JsonResult.success(byId);
    }


}
