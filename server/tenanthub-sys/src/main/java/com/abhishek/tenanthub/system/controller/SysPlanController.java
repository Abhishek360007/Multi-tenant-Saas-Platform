package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.common.exception.BizException;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.system.entity.SysPlan;
import com.abhishek.tenanthub.system.mapper.SysPlanMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.isAdminUser;
import static com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils.isMasterUser;

/**
 * Subscription plans. Everyone signed in can list offered plans; platform admins can edit them.
 *
 * @author Abhishek Raj
 */
@Tag(name = "[ System ] Subscription plans")
@RestController
@AllArgsConstructor
@RequestMapping("/sys/plan")
public class SysPlanController {

    private final SysPlanMapper planMapper;

    @Operation(summary = "List plans", description = "Offered plans (platform admins also see hidden ones)")
    @GetMapping
    public JsonResult<List<SysPlan>> list() {
        var wrapper = new LambdaQueryWrapper<SysPlan>().orderByAsc(SysPlan::getSort);
        if (!isPlatformAdmin()) {
            wrapper.eq(SysPlan::getStatus, 1);
        }
        return JsonResult.success(planMapper.selectList(wrapper));
    }

    @Operation(summary = "Add or update a plan", description = "Platform admins only. Prices in paise, before GST")
    @PostMapping
    public JsonResult<Boolean> save(@RequestBody @Validated SysPlan plan) {
        if (!isPlatformAdmin()) {
            throw new BizException("Only platform admins can change plans");
        }
        if (plan.getStatus() == null) {
            plan.setStatus((byte) 1);
        }
        if (plan.getSort() == null) {
            plan.setSort(0);
        }
        var exists = planMapper.selectById(plan.getPlanCode()) != null;
        var rows = exists ? planMapper.updateById(plan) : planMapper.insert(plan);
        return JsonResult.success(rows > 0);
    }

    private static boolean isPlatformAdmin() {
        return isMasterUser() && isAdminUser();
    }
}
