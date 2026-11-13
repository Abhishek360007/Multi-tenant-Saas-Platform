package com.abhishek.tenanthub.monitor.controller;

import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.monitor.entity.LmsLoginLog;
import com.abhishek.tenanthub.monitor.service.LmsLoginLogService;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Set;


/**
 * <p>
 * 监控 登录日志
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 3.0
 */


@Slf4j
@RestController
@RequestMapping("/lms/lgoin")
@Tag(name = "[ 监控 ] 登录日志")
@AllArgsConstructor
public class LmsLoginLogController {

    private final LmsLoginLogService baseService;


    @Operation(summary = "单个查询", description = "根据Id查询")
    @GetMapping("/{id}")
    public JsonResult<LmsLoginLog> findById(@PathVariable("id") String id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "分页", description = "分页 查询所有")
    @GetMapping("/query")
    public JsonResult<Pagination<LmsLoginLog>> pageQuery(SearchPO params) {
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
