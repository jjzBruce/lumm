package com.lumm.toolbox.pdf.sign;

import com.itextpdf.awt.geom.Rectangle2D;
import com.itextpdf.text.pdf.parser.ImageRenderInfo;
import com.itextpdf.text.pdf.parser.RenderListener;
import com.itextpdf.text.pdf.parser.TextRenderInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Pdf阅读监听器 - 查找关键字位置
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Getter
public class LocationRenderListener implements RenderListener {

    /**
     * 定位坐标的关键字
     */
    @Setter
    private String keyWord;
    /**
     * 关键字所在的页数
     */
    @Setter
    private int page;
    //所有匹配的项
    private List<MatchItem> matchItems = new ArrayList<>();
    //所有项
    private List<MatchItem> allItems = new ArrayList<>();


    /**
     * Called when a new text block is beginning (i.e. BT)
     *
     * @since iText 5.0.1
     */
    @Override
    public void beginTextBlock() {

    }

    /**
     * Called when text should be rendered
     *
     * @param renderInfo information specifying what to render
     */
    @Override
    public void renderText(TextRenderInfo renderInfo) {
        String text = renderInfo.getText();
        Rectangle2D.Float boundingRectange = renderInfo.getBaseline().getBoundingRectange();
        MatchItem matchItem = new MatchItem();
        matchItem.setContent(text);
        matchItem.setPageNum(page);
        matchItem.setX(boundingRectange.x);
        matchItem.setY(boundingRectange.y);
        if (null != text && !" ".equals(text)) {
            if (text.equalsIgnoreCase(keyWord)) {
                matchItems.add(matchItem);
            }
        }
        allItems.add(matchItem);
    }

    /**
     * Called when a text block has ended (i.e. ET)
     *
     * @since iText 5.0.1
     */
    @Override
    public void endTextBlock() {

    }

    /**
     * Called when image should be rendered
     *
     * @param renderInfo information specifying what to render
     * @since iText 5.0.1
     */
    @Override
    public void renderImage(ImageRenderInfo renderInfo) {

    }
}
