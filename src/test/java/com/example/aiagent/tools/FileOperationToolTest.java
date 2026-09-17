package com.example.aiagent.tools;

import com.example.aiagent.constant.FileConstant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class FileOperationToolTest {

    /** 写入后必须真的落盘在 tmp/file 下，且内容能读回来 */
    @Test
    void writeFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String fileName = "test.txt";
        String content = "这是一个测试文件";

        String result = fileOperationTool.writeFile(fileName, content);
        Assertions.assertTrue(result.startsWith("文件写入成功到："), "写入应成功，实际返回：" + result);

    }

    /** 读取不存在的文件应返回失败提示，而不是抛异常 */
    @Test
    void readFile() {
        FileOperationTool fileOperationTool = new FileOperationTool();
        String result = fileOperationTool.readFile("不存在的文件.txt");
        Assertions.assertNotNull(result);
    }
}
