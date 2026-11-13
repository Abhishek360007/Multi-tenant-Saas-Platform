package com.abhishek.tenanthub.system.controller;

import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.system.service.SysUserService;
import com.abhishek.tenanthub.system.vo.UpdateUserTenantVO;
import com.abhishek.tenanthub.system.vo.UserListTenantVO;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/sys/user")
@Tag(name = "[ 系统 ] 用户管理")
public class SysUserController {

    public final SysUserService baseService;


    @Operation(summary = "查询关联租户", description = "查询系统用户关联的租户")
    @GetMapping("/list/tenant")
    public JsonResult<UserListTenantVO> listHaveTenants(@RequestParam Long userId) {
        return JsonResult.success(
                baseService.listHaveTenants(userId)
        );
    }


    @NoRepeatSubmit
    @Operation(summary = "修改关联租户", description = "修改系统用户关联的租户")
    @PostMapping("/update/tenant")
    public JsonResult<Boolean> updateTenant(@RequestBody @Validated UpdateUserTenantVO params) {
        return JsonResult.success(
                baseService.updateTenant(params.getUserId(), params.getTenantIds())
        );
    }


}
