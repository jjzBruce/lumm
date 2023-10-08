package com.lumm.toolbox.pdf.html.freemaker;

import cn.hutool.core.io.IoUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * HTML生成器
 *
 * @author zhangj
 */
public class HtmlGenerator {

    private final Configuration config;

    public HtmlGenerator() {
        this.config = init();
    }

    /**
     * 设置 freemarker
     */
    private Configuration init() {
        Configuration config = new Configuration();
        config.setDefaultEncoding(StandardCharsets.UTF_8.toString());
        return config;
    }

    /**
     * Generate html string.
     *
     * @param templateName   模板名称
     * @param templateStream 模板文件流
     * @param object         对象
     * @return String
     */
    public String generate(String templateName, InputStream templateStream, Object object) throws IOException, TemplateException {
        BufferedWriter writer = null;
        String htmlContent;
        InputStreamReader templateReader = null;
        try {
            // 设置模板加载器，使用InputStream加载模板
            templateReader = new InputStreamReader(templateStream);
            Template tp = new Template(templateName, templateReader, config);

            StringWriter stringWriter = new StringWriter();
            writer = new BufferedWriter(stringWriter);

            tp.setEncoding(StandardCharsets.UTF_8.toString());
            tp.process(object, writer);
            htmlContent = stringWriter.toString();
            writer.flush();
        } catch (freemarker.core.InvalidReferenceException e) {
            throw new RuntimeException(String.format("模板生成报错，原因：%s", e.getMessage()));
        } finally {
            IoUtil.close(writer);
            IoUtil.close(templateReader);
        }
        return htmlContent;
    }

}
