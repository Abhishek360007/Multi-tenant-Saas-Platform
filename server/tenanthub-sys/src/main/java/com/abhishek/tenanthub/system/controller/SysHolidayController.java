package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.system.entity.SysHoliday;
import com.abhishek.tenanthub.system.mapper.SysHolidayMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.isAdminUser;
import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.isMasterUser;

/**
 * Indian public holidays. Seeded with the Central Government gazetted list (DoPT);
 * platform admins can add later years or restricted holidays.
 *
 * @author Abhishek Raj
 */
@Tag(name = "[ Common ] Indian public holidays")
@RestController
@AllArgsConstructor
@RequestMapping("/sys/holiday")
public class SysHolidayController {

    private final SysHolidayMapper holidayMapper;

    @Operation(summary = "List holidays", description = "Holidays of a year (default: current year), optionally one category")
    @GetMapping
    public JsonResult<List<SysHoliday>> list(@RequestParam(required = false) Integer year,
                                             @RequestParam(required = false) String category) {
        int y = year == null ? LocalDate.now().getYear() : year;
        var wrapper = new LambdaQueryWrapper<SysHoliday>()
                .between(SysHoliday::getHolidayDate, LocalDate.of(y, 1, 1), LocalDate.of(y, 12, 31))
                .eq(StringUtils.isNotBlank(category), SysHoliday::getCategory, category)
                .orderByAsc(SysHoliday::getHolidayDate);
        return JsonResult.success(holidayMapper.selectList(wrapper));
    }

    @Operation(summary = "Is holiday", description = "Whether a date is a holiday (yyyy-MM-dd)")
    @GetMapping("/check")
    public JsonResult<SysHoliday> check(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return JsonResult.success(holidayMapper.selectById(date));
    }

    @Operation(summary = "Add or update a holiday", description = "Platform admins only")
    @PostMapping
    public JsonResult<Boolean> save(@RequestBody @Validated SysHoliday holiday) {
        requirePlatformAdmin();
        if (StringUtils.isBlank(holiday.getCategory())) {
            holiday.setCategory("gazetted");
        }
        var exists = holidayMapper.selectById(holiday.getHolidayDate()) != null;
        var rows = exists ? holidayMapper.updateById(holiday) : holidayMapper.insert(holiday);
        return JsonResult.success(rows > 0);
    }

    @Operation(summary = "Delete a holiday", description = "Platform admins only")
    @DeleteMapping("/{date}")
    public JsonResult<Boolean> delete(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        requirePlatformAdmin();
        return JsonResult.success(holidayMapper.deleteById(date) > 0);
    }

    private static void requirePlatformAdmin() {
        if (!isMasterUser() || !isAdminUser()) {
            throw new BizException("Only platform admins can change holidays");
        }
    }
}
