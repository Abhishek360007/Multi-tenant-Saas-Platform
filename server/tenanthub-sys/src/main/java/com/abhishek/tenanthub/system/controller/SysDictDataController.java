package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.system.entity.SysDictData;
import com.abhishek.tenanthub.system.po.DictQueryPO;
import com.abhishek.tenanthub.system.service.SysDictDataService;
import com.abhishek.tenanthub.system.vo.DictDataVO;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Dictionary data controller
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/8 10:15
 */


@Slf4j
@Tag(name = "[ Common ] Dictionary data")
@RestController
@RequestMapping("/dict/data")
@AllArgsConstructor
public class SysDictDataController {

    private SysDictDataService baseService;

    @Operation(summary = "Id Query", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<SysDictData> getById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<SysDictData>> pageQuery(DictQueryPO params) {
        var page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @Operation(summary = "[Map] Query all", description = "Paged query of all")
    @GetMapping("/mapKeys")
    public JsonResult<Map<String, List<DictDataVO>>> mapKeys() {
        var result = baseService.mapByKeys();
        return JsonResult.success(result);
    }


    @Operation(summary = "Query by type key", description = "Query all by dictionary type")
    @GetMapping("/type/{key}")
    public JsonResult<List<SysDictData>> listByKey(@PathVariable("key") String key) {
        return JsonResult.success(baseService.listByKey(key));
    }


    @NoRepeatSubmit
    @Operation(summary = "Add", description = "Do not include id")
    @PostMapping()
    public JsonResult<SysDictData> save(@RequestBody @Validated(ValidCreate.class) SysDictData entity) {
        baseService.save(entity);
        return JsonResult.success(entity);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated(ValidUpdate.class) SysDictData entity) {
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
