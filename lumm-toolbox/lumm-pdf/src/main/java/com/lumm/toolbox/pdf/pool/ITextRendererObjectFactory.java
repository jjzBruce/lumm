package com.lumm.toolbox.pdf.pool;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.IOException;

/**
 * ITextRenderer对象工厂,提供性能,加载中文字体集(大小20M),故增加对象池
 *
 * @author zhangj
 */
@Slf4j
public class ITextRendererObjectFactory extends BasePooledObjectFactory<ITextRenderer> {

    /**
     * 初始化对象池
     */
    private final static GenericObjectPool itextRendererObjectPool = init();

    /**
     * 初始化对象池
     */
    private static GenericObjectPool init() {
        GenericObjectPool itextRendererObjectPool = new GenericObjectPool(new ITextRendererObjectFactory());
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        // 设置策略为先进先出
        config.setLifo(false);
        // 最大对象数为 15
        config.setMaxTotal(15);
        // 最大空闲对象数为 5
        config.setMaxIdle(5);
        // 最小空闲对象数为 1
        config.setMinIdle(1);
        // 最长等待时间是5秒，如果这个时间段内没有取到对象就抛出异常
        config.setMaxWaitMillis(5 * 1000);
        itextRendererObjectPool.setConfig(config);
        return itextRendererObjectPool;
    }

    @Override
    public ITextRenderer create() throws Exception {
        return createTextRenderer();
    }

    /**
     * 初始化
     *
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static synchronized ITextRenderer createTextRenderer()
            throws DocumentException, IOException {
        ITextRenderer renderer = new ITextRenderer();
        ITextFontResolver fontResolver = renderer.getFontResolver();
        addFonts(fontResolver);
        return renderer;
    }

    /**
     * 添加字体
     *
     * @param fontResolver
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static ITextFontResolver addFonts(ITextFontResolver fontResolver)
            throws DocumentException, IOException {
        fontResolver.addFont("fonts/ARIALUNI.TTF", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont("fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        fontResolver.addFont("fonts/simfang.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        return fontResolver;
    }

    @Override
    public PooledObject wrap(ITextRenderer obj) {
        return new DefaultPooledObject(obj);
    }

    /**
     * 获取对象池,使用对象工厂 后提供性能,能够支持 500线程 迭代10
     *
     * @return
     */
    public static GenericObjectPool getObjectPool() {
        return itextRendererObjectPool;
    }

}
