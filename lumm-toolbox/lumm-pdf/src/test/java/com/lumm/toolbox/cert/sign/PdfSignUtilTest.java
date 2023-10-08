package com.lumm.toolbox.cert.sign;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfSignatureAppearance;
import com.itextpdf.text.pdf.security.*;
import com.lumm.toolbox.pdf.sign.MatchItem;
import com.lumm.toolbox.pdf.sign.PdfSignUtil;
import com.lumm.toolbox.pdf.sign.SignatureInfo;
import org.junit.Test;

import java.io.*;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * Pdf标注工具类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class PdfSignUtilTest {

    @Test
    public void test() throws IOException {
        byte[] p12Bytes;
        String p12Path = Thread.currentThread().getContextClassLoader().getResource("sign/tomcat.p12").getPath();
        try (FileInputStream inputStream = new FileInputStream(p12Path);
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int bytesRead;
            byte[] data = new byte[1024];
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            p12Bytes = buffer.toByteArray();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 签章图片二进制
        byte[] imageBytes;
        try (InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("sign/certTest.png");
             ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int bytesRead;
            byte[] data = new byte[1024];
            while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            imageBytes = buffer.toByteArray();
        }


        try {
            char[] password = "123456".toCharArray();
            //将证书文件放入指定路径，并读取keystore ，获得私钥和证书链
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ByteArrayInputStream bais = new ByteArrayInputStream(p12Bytes);
            ks.load(bais, password);

            String alias = ks.aliases().nextElement();
            PrivateKey pk = (PrivateKey) ks.getKey(alias, password);
            Certificate[] chain = ks.getCertificateChain(alias);

            SignatureInfo info = new SignatureInfo();
            info.setReason("理由");
            info.setLocation("位置");
            info.setPk(pk);
            info.setChain(chain);
            info.setCertificationLevel(PdfSignatureAppearance.NOT_CERTIFIED);
            info.setDigestAlgorithm(DigestAlgorithms.SHA1);

            info.setImgBytes(imageBytes);
            info.setRenderingMode(PdfSignatureAppearance.RenderingMode.GRAPHIC);

            String src = Thread.currentThread().getContextClassLoader().getResource( "sign/test.pdf").getPath();
            String targetPath = "D:\\tmp\\sign\\output-100.pdf";
            PdfReader reader = new PdfReader(src);


            MatchItem matchItem = PdfSignUtil.getKeyWords(reader, "签章");
            info.setX(matchItem.getX());
            info.setY(matchItem.getY());
            info.setPageNum(matchItem.getPageNum());

            byte[] sign = PdfSignUtil.sign(reader, info);
            FileOutputStream outputStream = new FileOutputStream(targetPath);
            outputStream.write(sign);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
