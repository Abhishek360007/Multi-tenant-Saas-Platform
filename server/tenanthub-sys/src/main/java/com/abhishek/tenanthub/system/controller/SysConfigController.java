package com.abhishek.tenanthub.system.controller;


import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.system.entity.SysConfig;
import com.abhishek.tenanthub.system.service.SysConfigService;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/sys/config")
@AllArgsConstructor
@Tag(name = "[ Common ] Global settings")
public class SysConfigController {

    public final SysConfigService baseService;


    @Operation(summary = "Id Query", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<SysConfig> getById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Query all", description = " Query all")
    @GetMapping("/list")
    public JsonResult<List<SysConfig>> list() {
        var list = baseService.list();
        return JsonResult.success(list);
    }


    @Operation(summary = "Paged query", description = "Paged query")
    @GetMapping("/query")
    public JsonResult<Pagination<SysConfig>> pageQuery(SearchPO params) {
        var page = baseService.pageKeywords(params,
                List.of(SysConfig::getConfigName, SysConfig::getConfigKey)
        );
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @NoRepeatSubmit
    @Operation(summary = "Add", description = "Do not include id")
    @PostMapping()
    public JsonResult<Boolean> save(@RequestBody @Validated(ValidCreate.class) SysConfig entity) {
        var flag = baseService.save(entity);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated(ValidUpdate.class) SysConfig entity) {
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


}
