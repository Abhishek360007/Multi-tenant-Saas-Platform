package com.abhishek.tenanthub.monitor.controller;

import com.abhishek.tenanthub.common.util.JsonResult;
import com.abhishek.tenanthub.monitor.entity.ServerInfo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequestMapping("/server/info")
@AllArgsConstructor
@Tag(name = "[ Common ] Server")
public class ServerInfoController {


    @Operation(summary = "Server details", description = "Get server details: memory, CPU, disk")
    @GetMapping()
    public JsonResult<ServerInfo> info() throws Exception {
        var serverInfo = new ServerInfo();
        serverInfo.copyTo();
        return JsonResult.success(serverInfo);
    }


    @Operation(summary = "Test hello1", description = "Test hello1")
    @PreAuthorize("hasAuthority('abc')")
    @GetMapping("hello1")
    public JsonResult<String> hello1() throws Exception {
        return JsonResult.success("hello1");
    }


    @Operation(summary = "Test hello2", description = "Test hello1")
    @PreAuthorize("hasAuthority('pre_user:view')")
    @GetMapping("hello2")
    public JsonResult<String> hello2() throws Exception {
        throw new Exception("hello2");
        //return JsonResult.success("2");
    }


    @PreAuthorize("hasAuthority('pre_user:view')")
    @GetMapping("hello3")
    public JsonResult<String> hello3() throws Exception {
        throw new Exception("hello3");
        //return JsonResult.success("2");
    }


}
