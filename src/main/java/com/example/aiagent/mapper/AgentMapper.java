package com.example.aiagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.aiagent.model.entity.Agent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AgentMapper extends BaseMapper<Agent> {

    /**
     * 批量查询 Agent
     */
    List<Agent> selectByIdBatch(@Param("ids") List<String> ids);
}
