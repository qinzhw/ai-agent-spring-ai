package com.example.aiagent.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.aiagent.typehandler.JsonbTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 智能体配置实体
 */
@Data
@TableName(value = "agent", autoResultMap = true)
public class Agent {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String name;

    private String description;

    private String systemPrompt;

    private String model;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String allowedTools;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String allowedKbs;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String chatOptions;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
