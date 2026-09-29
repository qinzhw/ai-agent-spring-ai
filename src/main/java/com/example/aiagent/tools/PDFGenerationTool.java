package com.example.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.example.aiagent.agent.tool.RuntimeTool;
import com.example.aiagent.agent.tool.ToolType;
import com.example.aiagent.constant.FileConstant;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * PDF 生成工具
 */
@Slf4j
@Component
public class PDFGenerationTool implements RuntimeTool {

    @Override
    public String getName() {
        return "pdfGeneration";
    }

    @Override
    public String getDescription() {
        return "PDF 文件生成工具";
    }

    @Override
    public ToolType getType() {
        return ToolType.OPTIONAL;
    }

    /**
     * 加载中文字体，按优先级尝试：
     * 1. iText font-asian 内置 CID 字体（STSongStd-Light）
     * 2. Windows 系统字体（微软雅黑 / 宋体）
     * 3. 兜底 Helvetica（不支持中文）
     */
    private PdfFont loadChineseFont() {
        // 1. 尝试 iText font-asian CID 字体
        try {
            return PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H",
                    PdfFontFactory.EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        } catch (Exception e) {
            log.debug("CID 字体 STSongStd-Light 不可用", e);
        }
        // 2. 尝试 Windows 系统字体
        String[] systemFonts = {
                "C:/Windows/Fonts/msyh.ttc",   // 微软雅黑
                "C:/Windows/Fonts/simsun.ttc",  // 宋体
                "C:/Windows/Fonts/simhei.ttf",  // 黑体
        };
        for (String fontPath : systemFonts) {
            try {
                java.io.File fontFile = new java.io.File(fontPath);
                if (fontFile.exists()) {
                    return PdfFontFactory.createFont(fontPath,
                            PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
                }
            } catch (Exception e) {
                log.debug("系统字体 {} 不可用", fontPath);
            }
        }
        // 3. 兜底 Helvetica
        log.warn("未找到可用中文字体，PDF 中的中文可能无法正确显示");
        try {
            return PdfFontFactory.createFont("Helvetica");
        } catch (IOException e) {
            throw new RuntimeException("无法加载任何 PDF 字体", e);
        }
    }

    @Tool(description = "Generate a PDF file with given content")
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        try {
            // 创建目录
            FileUtil.mkdir(fileDir);
            // 创建 PdfWriter 和 PdfDocument 对象
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {
                // 加载中文字体（多级兜底）
                PdfFont font = loadChineseFont();
                document.setFont(font);
                // 创建段落
                Paragraph paragraph = new Paragraph(content);
                // 添加段落并关闭文档
                document.add(paragraph);
            }
            return "PDF generated successfully to: " + filePath;
        } catch (IOException e) {
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
