package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.util.WrapExcelUtils;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.system.entity.SysTenant;
import com.abhishek.tenanthub.system.po.CreateTenantPO;
import com.abhishek.tenanthub.system.po.TenantCreatedSuccessVO;
import com.abhishek.tenanthub.system.service.SysTenantService;
import com.abhishek.tenanthub.system.vo.TenantSimpleDataVO;
import com.abhishek.tenanthub.system.vo.UpdateTenantTemplateIdVO;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/sys/tenant")
@Tag(name = "[ System ] Tenants")
public class SysTenantController {

    public final SysTenantService baseService;


    @Operation(summary = "Id Query", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<SysTenant> getById(@PathVariable("id") String id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Simple get", description = "Get the basic fields by ID")
    @GetMapping("/simple")
    public JsonResult<SysTenant> getSimpleById(@RequestParam("id") String id) {
        return JsonResult.success(baseService.getSimpleById(id));
    }


    @Operation(summary = "Tenant ID exists", description = "Check whether a tenant ID exists")
    @GetMapping("/exist/{id}")
    public JsonResult<Boolean> existById(@PathVariable("id") String id) {
        return JsonResult.success(baseService.exist(SysTenant::getTenantId, id));
    }


    @Operation(summary = "Paged query", description = "Paged search of all")
    @GetMapping("/query")
    public JsonResult<Pagination<SysTenant>> pageQuery(SearchPO po) {
        var pageResult = baseService.pageKeywords(po);
        return JsonResult.success(
                pageResult.getTotal(),
                pageResult.getRecords()
        );
    }


    @Operation(summary = "Simple paged query", description = "Simple paged search of all")
    @GetMapping("/simple/query")
    public JsonResult<Pagination<SysTenant>> simplePageQuery(SearchPO po) {
        var pageResult = baseService.simplePageKeywords(po);
        return JsonResult.success(
                pageResult.getTotal(),
                pageResult.getRecords()
        );
    }


    @Operation(summary = "Simple search", description = "Simple search of all")
    @GetMapping("/simple/search")
    public JsonResult<Collection<TenantSimpleDataVO>> simpleSearchQuery(SearchPO po) {
        var pageResult = baseService.simpleSearchQuery(po);
        return JsonResult.success(pageResult);
    }


    @Operation(summary = "Export", description = "Export tenant data to Excel")
    @GetMapping("/list/export")
    public ResponseEntity<ByteArrayResource> listExport() {
        var result = baseService.list();
        return WrapExcelUtils.genPageWrite("Tenant data", result);
    }


    @NoRepeatSubmit
    @Operation(summary = "Add tenant", description = "Add a tenant")
    @PostMapping
    public JsonResult<TenantCreatedSuccessVO> save(@RequestBody @Validated CreateTenantPO po) {
        try {
            return JsonResult.success(baseService.create(po));
        } catch (Exception e) {
            return JsonResult.fail(e.getMessage(), TenantCreatedSuccessVO.class);
        }
    }


    @NoRepeatSubmit
    @Operation(summary = "Reset tenant data", description = "Reset tenant data")
    @PostMapping("/reset/data/{id}")
    public JsonResult<TenantCreatedSuccessVO> resetData(@PathVariable("id") String id) {
        return JsonResult.success(baseService.resetData(id));
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping
    public JsonResult<Boolean> updateById(@RequestBody @Validated(ValidUpdate.class) SysTenant tenant) {
        var flag = baseService.updateById(tenant);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Delete", description = "Only the ID is required")
    @DeleteMapping("/{id}")
    public JsonResult<Boolean> removeById(@PathVariable("id") String id) {
        var flag = baseService.removeById(id);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update templates", description = "Update tenant permissions")
    @PostMapping("/update/templates")
    public JsonResult<Boolean> updateTemplate(@RequestBody @Validated UpdateTenantTemplateIdVO params) {
        var flag = baseService.updateTemplate(params.getTenantId(), params.getTemplateIds());
        return JsonResult.success(flag);
    }


}
