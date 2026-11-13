package com.abhishek.tenanthub.perms.controller;

import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.perms.entity.PmsRole;
import com.abhishek.tenanthub.perms.po.BindingMenusVO;
import com.abhishek.tenanthub.perms.service.PmsRoleService;
import com.abhishek.tenanthub.perms.vo.UpdateMenusVO;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 3.0
 */


@Slf4j
@RestController
@RequestMapping("/pms/role")
@Tag(name = "[ Permissions ] Roles")
@AllArgsConstructor
public class PmsRoleController {

    private final PmsRoleService baseService;


    @Operation(summary = "Get one", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<PmsRole> getById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Query all", description = "Query all")
    @GetMapping("/list")
    public JsonResult<List<PmsRole>> list() {
        return JsonResult.success(baseService.list());
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<PmsRole>> pageQuery(SearchPO params) {
        var page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @NoRepeatSubmit
    @Operation(summary = "Add", description = "Do not include id")
    @PostMapping()
    public JsonResult<Boolean> save(@RequestBody @Validated({ValidCreate.class}) PmsRole entity) {
        var flag = baseService.save(entity);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated({ValidUpdate.class}) PmsRole entity) {
        var flag = baseService.updateById(entity);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Delete", description = "Only the ID is required")
    @DeleteMapping("/{id}")
    public JsonResult<Boolean> removeById(@PathVariable("id") Long id) {
        var flag = baseService.removeById(id);
        return JsonResult.success(flag);
    }


    @Operation(summary = "Assigned menus", description = "Query the menus assigned to a role")
    @GetMapping("/menus/{roleId}")
    public JsonResult<UpdateMenusVO> listHaveMenus(@PathVariable Long roleId) {
        var data = baseService.listHaveMenus(roleId);
        return JsonResult.success(data);
    }


    @Operation(summary = "Assigned departments", description = "Query the departments assigned to a role")
    @GetMapping("/depts/{roleId}")
    public JsonResult<UpdateMenusVO> listHaveDepts(@PathVariable Long roleId) {
        var data = baseService.listHaveDepts(roleId);
        return JsonResult.success(data);
    }


    @NoRepeatSubmit
    @Operation(summary = "Link menus", description = "Replaces the existing menus")
    @PostMapping("/menus")
    public JsonResult<Boolean> bindingMenus(@RequestBody @Validated BindingMenusVO entity) {
        var flag = baseService.updateMenus(entity.getId(), entity.getMenus());
        return JsonResult.success(flag);
    }

}
