package com.abhishek.tenanthub.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.po.SearchPO;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.system.entity.IndiaArea;
import com.abhishek.tenanthub.system.service.IndiaAreaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * India administrative areas Controller
 * </p>
 *
 * @author Wang Chen Chen
 * @version 1.0
 * @date 2021/7/5 16:37
 */

@Slf4j
@Tag(name = "[ Common ] India administrative areas")
@RestController
@AllArgsConstructor
@RequestMapping("/india/area")
public class IndiaAreaController {

    private IndiaAreaService baseService;

    @Operation(summary = "Get one", description = "Query by ID")
    @GetMapping("/{id}")
    public JsonResult<IndiaArea> getById(@PathVariable("id") Long id) {
        return JsonResult.success(baseService.getById(id));
    }


    @Operation(summary = "Query children", description = "Query children; 0 returns all states and UTs.")
    @GetMapping("/child/{parentCode}")
    public JsonResult<List<IndiaArea>> childList(@PathVariable Long parentCode) {
        if (parentCode == null || parentCode < 1) {
            parentCode = 0L;
        }
        var queryWrapper = new LambdaQueryWrapper<IndiaArea>();
        queryWrapper.eq(IndiaArea::getParentCode, parentCode);
        return JsonResult.success(baseService.list(queryWrapper));
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<IndiaArea>> pageQuery(SearchPO params) {
        IPage<IndiaArea> page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


}
