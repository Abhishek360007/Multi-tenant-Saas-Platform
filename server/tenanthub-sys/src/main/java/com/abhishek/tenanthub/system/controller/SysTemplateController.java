package com.abhishek.tenanthub.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.system.entity.SysTemplate;
import com.abhishek.tenanthub.system.service.SysTemplateService;
import com.abhishek.tenanthub.system.vo.BindingMenusVO;
import com.abhishek.tenanthub.system.vo.UpdateMenusVO;
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
 * Tenant permission template controller
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/8 10:15
 */


@Slf4j
@Tag(name = "[ System ] Tenant permission templates")
@RestController
@AllArgsConstructor
@RequestMapping("/sys/template")
public class SysTemplateController {

    private final SysTemplateService baseService;


    @Operation(summary = "Get one", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<SysTemplate> getById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Query all", description = " Query all")
    @GetMapping("/list")
    public JsonResult<List<SysTemplate>> list() {
        var list = baseService.list();
        return JsonResult.success(list);
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<SysTemplate>> pageQuery(SearchPO params) {
        IPage<SysTemplate> page = baseService.pageKeywords(params,
                List.of(SysTemplate::getName, SysTemplate::getCreateTime));
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @NoRepeatSubmit
    @Operation(summary = "Add", description = "Do not include id")
    @PostMapping()
    public JsonResult<Boolean> save(@RequestBody @Validated(ValidCreate.class) SysTemplate entity) {
        var save = baseService.save(entity);
        return JsonResult.success(save);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated(ValidUpdate.class) SysTemplate entity) {
        var flag = baseService.updateById(entity);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Delete", description = "Only the ID is required")
    @DeleteMapping("/{id}")
    public JsonResult<Boolean> removeById(@PathVariable Long id) {
        var flag = baseService.removeById(id);
        return JsonResult.success(flag);
    }


    @Operation(summary = "Template permissions", description = "Query the permissions of a template")
    @GetMapping("/menus/{id}")
    public JsonResult<UpdateMenusVO> listHaveMenus(@PathVariable Long id) {
        return JsonResult.success(baseService.listHaveMenus(id));
    }


    @NoRepeatSubmit
    @Operation(summary = "Update menus", description = "Update the menus of a permission template")
    @PostMapping("/menus")
    public JsonResult<Boolean> updateMenus(@RequestBody @Validated BindingMenusVO params) {
        var flag = baseService.updateMenus(params.getId(), params.getMenus());
        return JsonResult.success(flag);
    }


}
