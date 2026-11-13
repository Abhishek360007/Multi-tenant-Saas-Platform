package com.abhishek.saas.monitor.controller;

import com.abhishek.saas.common.domain.Pagination;
import com.abhishek.saas.common.po.SearchPO;
import com.abhishek.saas.common.util.JsonResult;
import com.abhishek.saas.monitor.entity.LmsOperLog;
import com.abhishek.saas.monitor.service.LmsOperLogService;
import com.abhishek.saas.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


/**
 * <p>
 * 监控 操作日志
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 3.0
 */


@Slf4j
@RestController
@RequestMapping("/lms/oper")
@Tag(name = "[ 监控 ] 操作日志")
@AllArgsConstructor
public class LmsOperLogController {

    private final LmsOperLogService baseService;


    @Operation(summary = "单个查询", description = "根据Id查询")
    @GetMapping("/{id}")
    public JsonResult<LmsOperLog> findById(@PathVariable("id") String id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "分页", description = "分页 查询所有")
    @GetMapping("/query")
    public JsonResult<Pagination<LmsOperLog>> pageQuery(SearchPO params) {
        var page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @NoRepeatSubmit
    @Operation(summary = "批量删除", description = "只需要id即可")
    @PostMapping()
    public JsonResult<Boolean> delete(@RequestBody Set<String> ids) {
        boolean b = baseService.removeBatchByIds(ids);
        return JsonResult.success(b);
    }


}
