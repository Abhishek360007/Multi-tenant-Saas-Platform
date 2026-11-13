package com.abhishek.tenanthub.corems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidDelete;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.corems.entity.CmsProject;
import com.abhishek.tenanthub.corems.po.ProjectQueryPO;
import com.abhishek.tenanthub.corems.service.CmsProjectService;
import com.abhishek.tenanthub.corems.vo.ResetPasswordVO;
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
 * Project controller
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/8 10:15
 */

@Slf4j
@Tag(name = "[ Core ] Projects")
@RestController
@RequestMapping("/cms/project")
@AllArgsConstructor
public class CmsProjectController {

    private CmsProjectService baseService;


    @Operation(summary = "Get one", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<CmsProject> findById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Simple paged query", description = "Simple paged search of all")
    @GetMapping("/simple/query")
    public JsonResult<Pagination<CmsProject>> simplePageQuery(SearchPO po) {
        var pageResult = baseService.simplePageKeywords(po);
        return JsonResult.success(
                pageResult.getTotal(),
                pageResult.getRecords()
        );
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<CmsProject>> pageQuery(ProjectQueryPO params) {
        IPage<CmsProject> page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @Operation(summary = "Query all", description = " Query all")
    @GetMapping("/list")
    public JsonResult<List<CmsProject>> list() {
        return JsonResult.success(baseService.list());
    }


    @NoRepeatSubmit
    @Operation(summary = "Add", description = "Do not include id")
    @PostMapping()
    public JsonResult<Boolean> save(@RequestBody @Validated(ValidCreate.class) CmsProject entity) {
        var flag = baseService.save(entity);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated(ValidUpdate.class) CmsProject entity) {
        return JsonResult.success(baseService.updateById(entity));
    }


    @NoRepeatSubmit
    @Operation(summary = "Delete", description = "Only the ID is required")
    @DeleteMapping()
    public JsonResult<Boolean> removeById(@Validated(ValidDelete.class) CmsProject entity) {
        return JsonResult.success(baseService.removeById(entity));
    }


    @NoRepeatSubmit
    @Operation(summary = "Reset project password", description = "Reset the project password")
    @PostMapping("/reset/password")
    public JsonResult<Boolean> resetPassword(@RequestBody @Validated ResetPasswordVO data) {
        return JsonResult.success(
                baseService.resetPassword(data)
        );
    }


}
