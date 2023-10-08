package com.lumm.toolbox.pdf.sign;

import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.security.MakeSignature;
import lombok.Data;

import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * 签章相关信息
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
@Data
public class SignatureInfo {

    /**
     * 理由
     */
    private String reason;

    /**
     * 位置
     */
    private String location;

    /**
     * 摘要类型
     */
    private String digestAlgorithm;

    /**
     * 图章路径
     */
    private byte[] imgBytes;

    /**
     * 坐标x
     */
    private float x;

    /**
     * 坐标y
     */
    private float y;

    /**
     * 所在页
     */
    private int pageNum;

    /**
     * 证书链
     */
    private Certificate[] chain;

    /**
     * 私钥
     */
    private PrivateKey pk;

    /**
     * 批准签章
     */
    private int certificationLevel = 0;

    /**
     * 表现形式：仅描述，仅图片，图片和描述，签章者和描述
     */
    private PdfSignatureAppearance.RenderingMode renderingMode;

    /**
     * 支持标准，CMS,CADES
     */
    private MakeSignature.CryptoStandard subFilter;

}
