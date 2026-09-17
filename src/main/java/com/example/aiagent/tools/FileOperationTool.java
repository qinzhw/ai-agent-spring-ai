package com.example.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.example.aiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * 文件操作工具类（提供读写文件的功能）
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read the content of a file")
    public String readFile(@ToolParam(description = "The name of the file to read") String fileName) {
        String filePath = FILE_DIR + "/" + fileName;
        try{
            return FileUtil.readUtf8String(filePath);
        }catch (Exception e){
            return "读取文件失败：" + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "The name of the file to write to") String fileName,
                            @ToolParam(description = "The content to write to the file") String content) {
        String filePath = FILE_DIR + "/" + fileName;
        try{
            // 创建目录
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(filePath, content);
            return "文件写入成功到：" + filePath;
        }catch (Exception e){
            return "文件写入失败：" + e.getMessage();
        }

    }
}
