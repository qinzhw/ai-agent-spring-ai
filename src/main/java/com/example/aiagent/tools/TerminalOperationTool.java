package com.example.aiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 终端操作工具
 */
public class TerminalOperationTool {
    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(
            @ToolParam(description = "Command to execute in the terminal")
            String command) {
        StringBuilder output = new StringBuilder();
        try {
            // 启动子进程，执行传入的命令字符串
//            Process process = Runtime.getRuntime().exec(command);
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", command);
            Process process = builder.start();

            // 读取进程标准输出 stdout
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            // 等待命令执行结束，获取退出码
            int exitCode = process.waitFor();
            // 非0代表命令执行异常
            if (exitCode != 0) {
                output.append("Command execution failed with exit code: ").append(exitCode);
            }
        } catch (IOException | InterruptedException e) {
            // IO异常 / 线程中断异常捕获
            output.append("Error executing command: ").append(e.getMessage());
        }
        // 返回命令执行结果文本给大模型
        return output.toString();
    }
}

