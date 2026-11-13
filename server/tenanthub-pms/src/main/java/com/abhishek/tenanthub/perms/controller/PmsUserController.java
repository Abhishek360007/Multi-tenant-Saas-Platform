package com.abhishek.tenanthub.perms.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.abhishek.tenanthub.auth.jwt.JwtLoginUser;
import com.abhishek.tenanthub.auth.jwt.JwtSecurityUtils;
import com.abhishek.tenanthub.auth.service.JwtTokenService;
import com.abhishek.tenanthub.common.domain.Pagination;
import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.common.valid.ValidCreate;
import com.abhishek.tenanthub.common.valid.ValidUpdate;
import com.abhishek.tenanthub.perms.entity.PmsUser;
import com.abhishek.tenanthub.perms.po.UserQueryPO;
import com.abhishek.tenanthub.perms.service.PmsUserService;
import com.abhishek.tenanthub.perms.vo.ResetPasswordVO;
import com.abhishek.tenanthub.perms.vo.UpdatePasswordVO;
import com.abhishek.tenanthub.perms.vo.UpdateUserRoleIdVO;
import com.abhishek.tenanthub.perms.vo.UserRightsVO;
import com.abhishek.tenanthub.web.repeat.NoRepeatSubmit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

import static com.abhishek.tenanthub.tenant.util.DelegateUtils.delegate;


/**
 * <p>
 * </p>
 *
 * @author Wang Chen Chen <932560435@qq.com>
 * @version 3.0
 */


@Slf4j
@RestController
@RequestMapping("/pms/user")
@Tag(name = "[ Permissions ] Users")
@AllArgsConstructor
public class PmsUserController {

    private final PmsUserService baseService;

    private final JwtTokenService jwtTokenService;


    @Operation(summary = "User permissions", description = "User permissions")
    @GetMapping("/rights")
    public JsonResult<UserRightsVO> getUserRights() {
        var result = baseService.getUserRights();
        return JsonResult.success(result);
    }


    @Operation(summary = "Paged query", description = "Paged query of all")
    @GetMapping("/query")
    public JsonResult<Pagination<PmsUser>> pageQuery(UserQueryPO params) {
        IPage<PmsUser> page = baseService.pageKeywords(params);
        return JsonResult.success(page.getTotal(), page.getRecords());
    }


    @Operation(summary = "Online users", description = "Query all online users")
    @GetMapping("/online/query")
    public JsonResult<Collection<JwtLoginUser>> onlineQuery() {
        return JsonResult.success(jwtTokenService.mapLoginUser().values());
    }


    @NoRepeatSubmit
    @Operation(summary = "Add", description = "Do not include id")
    @PostMapping()
    public JsonResult<PmsUser> save(@RequestBody @Validated({ValidCreate.class}) PmsUser entity) {
        baseService.save(entity);
        return JsonResult.success(entity);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping()
    public JsonResult<Boolean> updateById(@RequestBody @Validated({ValidUpdate.class}) PmsUser entity) {
        var flag = baseService.updateById(entity);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Update", description = "Update requires id")
    @PutMapping("/info")
    public JsonResult<Boolean> updateInfo(@RequestBody @Validated({ValidUpdate.class}) PmsUser entity) {
        // Uses the tenant ID of the signed-in user. Update
        var flag = delegate(JwtSecurityUtils.getTenantId(),
                () -> baseService.updateById(entity));
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Delete", description = "Update requires id")
    @DeleteMapping("/{id}")
    public JsonResult<Boolean> removeById(@PathVariable Long id) {
        var flag = baseService.removeById(id);
        return JsonResult.success(flag);
    }


    @NoRepeatSubmit
    @Operation(summary = "Change password", description = "Change the user password")
    @PostMapping("/update/password")
    public JsonResult<Boolean> updatePassword(@RequestBody @Validated UpdatePasswordVO data) {
        return JsonResult.success(
                baseService.updatePassword(data)
        );
    }


    @NoRepeatSubmit
    @Operation(summary = "Reset password", description = "Reset the user password")
    @PostMapping("/reset/password")
    public JsonResult<Boolean> resetPassword(@RequestBody @Validated ResetPasswordVO data) {
        return JsonResult.success(
                baseService.resetPassword(data)
        );
    }


    @NoRepeatSubmit
    @Operation(summary = "Update user roles", description = "Update the user roles; replaces the previous roles.")
    @PostMapping("/update/roles")
    public JsonResult<Boolean> updateUserRoles(@RequestBody @Validated UpdateUserRoleIdVO data) {
        return JsonResult.success(
                baseService.updateUserRoles(data.getUserId(), data.getRoles())
        );
    }


}
