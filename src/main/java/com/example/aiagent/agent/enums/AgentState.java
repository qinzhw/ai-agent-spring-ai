package com.example.aiagent.agent.enums;

/**
 * 代理执行状态的枚举类
 */
public enum AgentState {

    /** 空闲状态 */
    IDLE,

    /** 规划中 */
    PLANNING,

    /** 思考中 */
    THINKING,

    /** 执行中 */
    EXECUTING,

    /** 运行中 */
    RUNNING,

    /** 已完成 */
    FINISHED,

    /** 错误结束 */
    ERROR
}
