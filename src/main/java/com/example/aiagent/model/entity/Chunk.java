package com.example.aiagent.model.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.aiagent.typehandler.JsonbTypeHandler;
import com.example.aiagent.typehandler.PgVectorTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档分块向量实体（pgvector）
 */
@Data
@TableName(value = "chunk_bge_m3", autoResultMap = true)
public class Chunk {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String kbId;

    private String docId;

    private String content;

    @TableField(typeHandler = JsonbTypeHandler.class)
    private String metadata;

    @TableField(typeHandler = PgVectorTypeHandler.class)
    private float[] embedding;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
