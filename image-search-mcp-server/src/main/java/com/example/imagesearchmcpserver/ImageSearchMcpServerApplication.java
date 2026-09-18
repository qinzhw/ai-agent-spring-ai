package com.example.imagesearchmcpserver;

import com.example.imagesearchmcpserver.tools.ImageSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ImageSearchMcpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ImageSearchMcpServerApplication.class, args);
	}

	/**
	 * 配置图片搜索工具
	 * @param imageSearchTool
	 * @return
	 */
	@Bean
	public ToolCallbackProvider imageSearchTools(ImageSearchTool imageSearchTool) {
		// 扫描传入对象里面所有带 `@Tool` 的方法，自动注册成 MCP 对外暴露的工具
		return MethodToolCallbackProvider.builder()
				.toolObjects(imageSearchTool)
				.build();
	}

}

