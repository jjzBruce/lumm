package com.lumm.toolbox.cert.sign;

import cn.hutool.core.io.IoUtil;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.security.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.List;
import java.util.UUID;

/**
 * Pdf标注工具类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class PdfSignUtil {

    /**
     * 获取关键字所在PDF坐标
     *
     * @param pdfReader 文件路径
     * @param keyWords  关键字
     * @return MatchItem
     */
    public static MatchItem getKeyWords(PdfReader pdfReader, String keyWords) {
        int page = 0;
        try {
            int pageNum = pdfReader.getNumberOfPages();
            PdfReaderContentParser pdfReaderContentParser = new PdfReaderContentParser(pdfReader);
            LocationRenderListener renderListener = new LocationRenderListener();
            renderListener.setKeyWord(keyWords);
            StringBuilder allText;
            for (page = 1; page <= pageNum; page++) {
                renderListener.setPage(page);
                pdfReaderContentParser.processContent(page, renderListener);
                List<MatchItem> matchItems = renderListener.getMatchItems();
                if (matchItems != null && matchItems.size() > 0) {
                    //完全匹配
                    return matchItems.get(0);
                }
                List<MatchItem> allItems = renderListener.getAllItems();
                allText = new StringBuilder();
                for (MatchItem item : allItems) {
                    allText.append(item.getContent());
                    //关键字存在连续多个块中
                    if (allText.indexOf(keyWords) != -1) {
                        return item;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 单多次签章通用
     *
     * @param reader         PdfReader
     * @param signatureInfos
     */
    public static byte[] sign(PdfReader reader, SignatureInfo... signatureInfos) {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        try {
            for (SignatureInfo signatureInfo : signatureInfos) {
                ByteArrayOutputStream tempArrayOutputStream = new ByteArrayOutputStream();
                //创建签章工具PdfStamper ，最后一个boolean参数是否允许被追加签名
                PdfStamper stamper = PdfStamper.createSignature(reader, tempArrayOutputStream, '\0', null, true);
                // 获取数字签章属性对象
                PdfSignatureAppearance appearance = stamper.getSignatureAppearance();
                appearance.setReason(signatureInfo.getReason());
                appearance.setLocation(signatureInfo.getLocation());
                //设置签名的签名域名称，多次追加签名的时候，签名预名称不能一样，图片大小受表单域大小影响（过小导致压缩）
                float x = signatureInfo.getX(), y = signatureInfo.getY();
                int size = 120;
                // 是对应x轴和y轴坐标
                float lly = y - 50;
                appearance.setVisibleSignature(
                        new Rectangle(x, lly, x + size, lly + size), signatureInfo.getPageNum(),
                        UUID.randomUUID().toString().replaceAll("-", ""));

                //读取图章图片
                Image image = Image.getInstance(signatureInfo.getImgBytes());
                appearance.setSignatureGraphic(image);
                appearance.setCertificationLevel(signatureInfo.getCertificationLevel());
                //设置图章的显示方式，如下选择的是只显示图章（还有其他的模式，可以图章和签名描述一同显示）
                appearance.setRenderingMode(signatureInfo.getRenderingMode());
                // 摘要算法
                ExternalDigest digest = new BouncyCastleDigest();
                // 签名算法
                ExternalSignature signature = new PrivateKeySignature(signatureInfo.getPk(), signatureInfo.getDigestAlgorithm(), null);
                // 调用itext签名方法完成pdf签章
                MakeSignature.signDetached(appearance, digest, signature, signatureInfo.getChain(), null, null, null, 0, signatureInfo.getSubFilter());
                //定义输入流为生成的输出流内容，以完成多次签章的过程
                result = tempArrayOutputStream;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(result);
        }
        return result.toByteArray();
    }

    /**
     * 单多次签章通用
     *
     * @param reader      PdfReader
     * @param signKeyWord 签章关键字
     */
    public static byte[] sign(PdfReader reader, byte[] p12Bytes, String p12Pwd, String signKeyWord, byte[] imgBytes) throws Exception {
        char[] password = p12Pwd.toCharArray();
        //将证书文件放入指定路径，并读取keystore ，获得私钥和证书链
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ByteArrayInputStream bais = new ByteArrayInputStream(p12Bytes);
        ks.load(bais, password);
        String alias = ks.aliases().nextElement();
        PrivateKey pk = (PrivateKey) ks.getKey(alias, password);
        Certificate[] chain = ks.getCertificateChain(alias);

        MatchItem matchItem = getKeyWords(reader, signKeyWord);

        // 创建签章信息类
        SignatureInfo info = new SignatureInfo();
        info.setReason("理由");
        info.setLocation("位置");
        info.setPk(pk);
        info.setChain(chain);
        info.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
        info.setDigestAlgorithm(DigestAlgorithms.SHA1);
        info.setImgBytes(imgBytes);
        info.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);
        info.setX(matchItem.getX());
        info.setY(matchItem.getY());
        info.setPageNum(matchItem.getPageNum());

        return sign(reader, info);
    }
}
