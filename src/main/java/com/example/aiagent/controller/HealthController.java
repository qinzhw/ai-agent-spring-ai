package com.example.aiagent.controller;

import com.example.aiagent.common.BaseResponse;
import com.example.aiagent.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

    /**
     * 健康检查
     */
    @GetMapping
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("ok");
    }
}
