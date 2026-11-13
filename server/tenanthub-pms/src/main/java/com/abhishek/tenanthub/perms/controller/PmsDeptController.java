package com.abhishek.tenanthub.perms.controller;

import cn.hutool.core.lang.tree.Tree;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchParentPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.perms.entity.PmsDept;
import com.abhishek.tenanthub.perms.service.PmsDeptService;
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
 * Department controller
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/8 10:15
 */

@Slf4j
@Tag(name = "[ Permissions ] Departments")
@RestController
@RequestMapping("/pms/dept")
@AllArgsConstructor
public class PmsDeptController {

    private PmsDeptService baseService;


    @Operation(summary = "Get one", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<PmsDept> getById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<PmsDept>> pageQuery(SearchParentPO params) {
        IPage<PmsDept> page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @Operation(summary = "Query all", description = " Query all")
    @GetMapping("/list")
    public JsonResult<List<PmsDept>> list() {
        return JsonResult.success(baseService.list());
    }


    @Operation(summary = "Query tree nodes", description = "Query all [as a tree]")
    @GetMapping("/tree")
    public JsonResult<List<Tree<Long>>> tree() {
        return JsonResult.success(baseService.treeNode());
    }


    @NoRepeatSubmit
    @Operation(summary = "Add", description = "Do not include id")
    @PostMapping()
    public JsonResult<Boolean> save(@RequestBody @Validated({ValidCreate.class}) PmsDept entity) {
        var flag = baseService.save(entity);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated({ValidUpdate.class}) PmsDept entity) {
        return JsonResult.success(baseService.updateById(entity));
    }


    @NoRepeatSubmit
    @Operation(summary = "Delete", description = "Only the ID is required")
    @DeleteMapping("/{id}")
    public JsonResult<Boolean> removeById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.removeById(id));
    }


}
