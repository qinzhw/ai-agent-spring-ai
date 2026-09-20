package com.example.aiagent.dto;

/**
 * 恋爱指导报告（结构化输出）
 *
 * @param title   报告标题
 * @param content 报告内容（分点建议）
 */
public record LoveReport(String title, String content) {
}
