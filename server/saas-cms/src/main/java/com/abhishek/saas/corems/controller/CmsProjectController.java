package com.abhishek.saas.corems.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.saas.common.domain.Pagination;
import com.abhishek.saas.common.po.SearchPO;
import com.abhishek.saas.common.util.JsonResult;
import com.abhishek.saas.common.valid.ValidCreate;
import com.abhishek.saas.common.valid.ValidDelete;
import com.abhishek.saas.common.valid.ValidUpdate;
import com.abhishek.saas.corems.entity.CmsProject;
import com.abhishek.saas.corems.po.ProjectQueryPO;
import com.abhishek.saas.corems.service.CmsProjectService;
import com.abhishek.saas.corems.vo.ResetPasswordVO;
import com.abhishek.saas.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 项目 控制器
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/8 10:15
 */

@Slf4j
@Tag(name = "[ 核心 ] 项目")
@RestController
@RequestMapping("/cms/project")
@AllArgsConstructor
public class CmsProjectController {

    private CmsProjectService baseService;


    @Operation(summary = "单个查询", description = "根据Id查询")
    @GetMapping("/{id}")
    public JsonResult<CmsProject> findById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "简单分页查询", description = "简单分页 搜索查询所有")
    @GetMapping("/simple/query")
    public JsonResult<Pagination<CmsProject>> simplePageQuery(SearchPO po) {
        var pageResult = baseService.simplePageKeywords(po);
        return JsonResult.success(
                pageResult.getTotal(),
                pageResult.getRecords()
        );
    }


    @Operation(summary = "分页", description = "分页 查询所有")
    @GetMapping("/query")
    public JsonResult<Pagination<CmsProject>> pageQuery(ProjectQueryPO params) {
        IPage<CmsProject> page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @Operation(summary = "查询所有", description = " 查询所有")
    @GetMapping("/list")
    public JsonResult<List<CmsProject>> list() {
        return JsonResult.success(baseService.list());
    }


    @NoRepeatSubmit
    @Operation(summary = "新增", description = "不需要添加id")
    @PostMapping()
    public JsonResult<Boolean> save(@RequestBody @Validated(ValidCreate.class) CmsProject entity) {
        var flag = baseService.save(entity);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "修改", description = "修改必须要id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated(ValidUpdate.class) CmsProject entity) {
        return JsonResult.success(baseService.updateById(entity));
    }


    @NoRepeatSubmit
    @Operation(summary = "删除", description = "只需要id即可")
    @DeleteMapping()
    public JsonResult<Boolean> removeById(@Validated(ValidDelete.class) CmsProject entity) {
        return JsonResult.success(baseService.removeById(entity));
    }


    @NoRepeatSubmit
    @Operation(summary = "重置项目密码", description = "重置项目密码")
    @PostMapping("/reset/password")
    public JsonResult<Boolean> resetPassword(@RequestBody @Validated ResetPasswordVO data) {
        return JsonResult.success(
                baseService.resetPassword(data)
        );
    }


}
