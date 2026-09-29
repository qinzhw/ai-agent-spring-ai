package com.example.aiagent.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 运行时工具视图对象
 * 暴露给前端，用于动态展示 Agent 可选工具
 */
@Data
@Builder
public class RuntimeToolVO {
    private String name;
    private String description;
    private String type;
}
