package com.abhishek.tenanthub.monitor.controller;

import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.monitor.entity.LmsOperLog;
import com.abhishek.tenanthub.monitor.service.LmsOperLogService;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


/**
 * <p>
 * Operation log monitor
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 3.0
 */


@Slf4j
@RestController
@RequestMapping("/lms/oper")
@Tag(name = "[ Monitor ] Operation logs")
@AllArgsConstructor
public class LmsOperLogController {

    private final LmsOperLogService baseService;


    @Operation(summary = "Get one", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<LmsOperLog> findById(@PathVariable("id") String id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<LmsOperLog>> pageQuery(SearchPO params) {
        var page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @NoRepeatSubmit
    @Operation(summary = "Delete selected", description = "Only the IDs are required")
    @PostMapping()
    public JsonResult<Boolean> delete(@RequestBody Set<String> ids) {
        boolean b = baseService.removeBatchByIds(ids);
        return JsonResult.success(b);
    }


}
