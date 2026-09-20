package com.example.aiagent.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 终端操作工具
 */
@Component
public class TerminalOperationTool {

    /** 命令执行超时时间（秒） */
    private static final long TIMEOUT_SECONDS = 30;

    @Tool(description = "Execute a command in the terminal")
    public String executeTerminalCommand(
            @ToolParam(description = "Command to execute in the terminal")
            String command) {
        StringBuilder output = new StringBuilder();
        Process process = null;
        try {
            // 启动子进程，执行传入的命令字符串
            ProcessBuilder builder = new ProcessBuilder("cmd", "/c", command);
            // 合并 stderr 到 stdout，避免子进程因 stderr 缓冲区满而阻塞
            builder.redirectErrorStream(true);
            process = builder.start();

            // 读取进程输出（已合并 stdout + stderr）
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }
            // 等待命令执行结束，获取退出码
            boolean finished = process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                output.append("\nCommand execution timed out after ").append(TIMEOUT_SECONDS).append(" seconds");
            } else {
                int exitCode = process.exitValue();
                // 非0代表命令执行异常
                if (exitCode != 0) {
                    output.append("Command execution failed with exit code: ").append(exitCode);
                }
            }
        } catch (IOException | InterruptedException e) {
            // IO异常 / 线程中断异常捕获
            output.append("Error executing command: ").append(e.getMessage());
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
        // 返回命令执行结果文本给大模型
        return output.toString();
    }
}

