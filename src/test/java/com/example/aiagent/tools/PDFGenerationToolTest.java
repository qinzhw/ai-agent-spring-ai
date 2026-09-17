package com.example.aiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PDFGenerationToolTest {

    @Test
    public void testGeneratePDF() {
        PDFGenerationTool tool = new PDFGenerationTool();
        String fileName = "TEST.pdf";
        String content = "这是一个测试PDF文件";
        String result = tool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}
