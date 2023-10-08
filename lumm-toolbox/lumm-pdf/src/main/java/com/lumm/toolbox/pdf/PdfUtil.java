package com.lumm.toolbox.pdf;

import cn.hutool.core.util.StrUtil;
import com.lumm.toolbox.pdf.html.freemaker.HtmlGenerator;
import com.lumm.toolbox.pdf.pool.ITextRendererObjectFactory;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Pdf工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Slf4j
public abstract class PdfUtil {

    /**
     * html生成器
     */
    private final static HtmlGenerator htmlGenerator = new HtmlGenerator();

    /**
     * 使用模板,模板数据,生成pdf
     *
     * @param templateStream 模板文件流
     * @return boolean
     */
    public static boolean htmlTemplate2Pdf(String templateName, InputStream templateStream, Object object, OutputStream outputStream) {
        try {
            String htmlContent = htmlGenerator.generate(templateName, templateStream, object);
            html2Pdf(htmlContent, outputStream);
            log.info("文件(模板为: {})生成成功", templateName);
        } catch (Exception e) {
            String error = String.format("文件(模板为: %s)生成失败", templateName);
            log.error(error);
            throw new IllegalStateException(error, e);
        }

        return true;
    }

    /**
     * 生成PDF
     *
     * @param htmlContent  html页面
     * @param outputStream 输出流
     * @throws Exception
     */
    public static void html2Pdf(String htmlContent, OutputStream outputStream) throws Exception {
        ITextRenderer iTextRenderer = null;
        try {
            // 读取HTML内容并替换所有的&nbsp;为空格
            htmlContent = StrUtil.replace(htmlContent, "&nbsp;", " "); // 替换&nbsp;为空格

            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(htmlContent.getBytes(StandardCharsets.UTF_8)));
            //获取对象池中对象
            iTextRenderer = (ITextRenderer) ITextRendererObjectFactory.getObjectPool().borrowObject();
            iTextRenderer.setDocument(doc, null);
            iTextRenderer.layout();
            iTextRenderer.createPDF(outputStream);
        } catch (Exception e) {
            ITextRendererObjectFactory.getObjectPool().invalidateObject(iTextRenderer);
            iTextRenderer = null;
            throw new RuntimeException(String.format("解析Html失败，原因: %s", e.getMessage()), e);
        } finally {
            if (iTextRenderer != null) {
                try {
                    ITextRendererObjectFactory.getObjectPool().returnObject(iTextRenderer);
                } catch (Exception ex) {
                    log.error("Cannot return object from pool.", ex);
                }
            }
        }
    }


}
