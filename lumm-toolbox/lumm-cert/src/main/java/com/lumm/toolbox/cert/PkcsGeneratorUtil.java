package com.lumm.toolbox.cert;

import cn.hutool.core.io.IoUtil;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 证书 - PKCS12 生成工具类
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since
 */
public class PkcsGeneratorUtil {
    private static KeyPair getKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
        generator.initialize(1024);
        // 证书中的密钥 公钥和私钥
        KeyPair keyPair = generator.generateKeyPair();
        return keyPair;
    }

    /**
     * 创建证书二进制
     *
     * @param password       密码
     * @param issuerStr      颁发机构信息
     * @param subjectStr     使用者信息
     * @param certificateCRL 颁发地址
     * @return byte [ ]
     */
    public static byte[] createCert(String password, String issuerStr, String subjectStr, String certificateCRL) {
        Map<String, byte[]> result = new HashMap<>();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // 标志生成PKCS12证书
            KeyStore keyStore = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
            keyStore.load(null, null);
            KeyPair keyPair = getKey();
            // issuer与 subject相同的证书就是CA证书
            Certificate cert = generateCertificateV3(issuerStr, subjectStr, keyPair, result, certificateCRL, null);
            // cretkey 随便写，标识别名
            keyStore.setKeyEntry("cretkey", keyPair.getPrivate(), password.toCharArray(), new Certificate[]{cert});
            cert.verify(keyPair.getPublic());
            keyStore.store(out, password.toCharArray());
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 生成X.509证书
     *
     * @param issuerStr      证书颁发者的名称字符串
     * @param subjectStr     证书主体的名称字符串
     * @param keyPair        包含公钥和私钥的密钥对
     * @param result         用于存储生成的证书和其他相关信息
     * @param certificateCRL 证书撤销列表（CRL）分发点的统一资源标识符（URI）
     * @param extensions     一个包含扩展信息的列表，可以为空
     * @return
     */
    public static Certificate generateCertificateV3(String issuerStr,
                                                    String subjectStr, KeyPair keyPair, Map<String, byte[]> result,
                                                    String certificateCRL, List<Extension> extensions) {
        ByteArrayInputStream bout = null;
        X509Certificate cert = null;
        try {
            // 获取密钥对中的公钥和私钥，并设置证书的有效期（一年）和序列号（随机生成）
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();
            Date notBefore = new Date();
            Calendar rightNow = Calendar.getInstance();
            rightNow.setTime(notBefore);
            // 日期加1年
            rightNow.add(Calendar.YEAR, 1);
            Date notAfter = rightNow.getTime();
            // 证书序列号
            BigInteger serial = BigInteger.probablePrime(256, new Random());

            // 创建一个X.509版本3证书构建器，其中包含颁发者、序列号、有效期、主体、以及公钥等信息
            X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                    new X500Name(issuerStr), serial, notBefore, notAfter,
                    new X500Name(subjectStr), publicKey);
            // 创建一个签名器，并将其与私钥关联，用于对证书进行签名
            JcaContentSignerBuilder jBuilder = new JcaContentSignerBuilder("SHA1withRSA");
            SecureRandom secureRandom = new SecureRandom();
            jBuilder.setSecureRandom(secureRandom);
            ContentSigner singer = jBuilder.setProvider(
                    new BouncyCastleProvider()).build(privateKey);
            // 分发点
            ASN1ObjectIdentifier cRLDistributionPoints = new ASN1ObjectIdentifier("2.5.29.31");
            GeneralName generalName = new GeneralName(GeneralName.uniformResourceIdentifier, certificateCRL);
            GeneralNames seneralNames = new GeneralNames(generalName);
            DistributionPointName distributionPoint = new DistributionPointName(seneralNames);
            DistributionPoint[] points = new DistributionPoint[1];
            points[0] = new DistributionPoint(distributionPoint, null, null);
            CRLDistPoint cRLDistPoint = new CRLDistPoint(points);
            builder.addExtension(cRLDistributionPoints, true, cRLDistPoint);
            // 用途
            ASN1ObjectIdentifier keyUsage = new ASN1ObjectIdentifier("2.5.29.15");
            // | KeyUsage.nonRepudiation | KeyUsage.keyCertSign
            builder.addExtension(keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyEncipherment));
            // 基本限制 X509Extension.java
            ASN1ObjectIdentifier basicConstraints = new ASN1ObjectIdentifier("2.5.29.19");
            builder.addExtension(basicConstraints, true, new BasicConstraints(true));
            // privKey:使用自己的私钥进行签名，CA证书
            if (extensions != null) {
                for (Extension ext : extensions) {
                    builder.addExtension(
                            new ASN1ObjectIdentifier(ext.getOid()),
                            ext.isCritical(),
                            ASN1Primitive.fromByteArray(ext.getValue()));
                }
            }
            X509CertificateHolder holder = builder.build(singer);
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            bout = new ByteArrayInputStream(holder.toASN1Structure().getEncoded());
            cert = (X509Certificate) cf.generateCertificate(bout);
            byte[] certBuf = holder.getEncoded();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            // 证书数据
            result.put("certificateData", certBuf);
            //公钥
            result.put("publicKey", publicKey.getEncoded());
            //私钥
            result.put("privateKey", privateKey.getEncoded());
            //证书有效开始时间
            result.put("notBefore", format.format(notBefore).getBytes("utf-8"));
            //证书有效结束时间
            result.put("notAfter", format.format(notAfter).getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(bout);
        }
        return cert;
    }

    public static void main(String[] args) throws Exception {
        // CN: 名字与姓氏    OU : 组织单位名称
        // O ：组织名称  L : 城市或区域名称  E : 电子邮件
        // ST: 州或省份名称  C: 单位的两字母国家代码
        String issuerStr = "CN=测试,OU=研发部,O=测试,C=CN,E=test@email.com,L=杭州,ST=浙江";
        String subjectStr = "CN=huangjinjin,OU=gitbook研发部,O=gitbook有限公司,C=CN,E=huangjinjin@sina.com,L=北京,ST=北京";
        String certificateCRL = "https://demo.com";
        byte[] certByte = createCert("123456", issuerStr, subjectStr, certificateCRL);

        FileOutputStream outPutStream = new FileOutputStream("D:\\tmp\\sign\\keystoreTest.p12");
        outPutStream.write(certByte);
        outPutStream.close();
        FileOutputStream fos = new FileOutputStream("D:\\tmp\\sign\\keystoreTest.cer");
        fos.write(certByte);
        fos.flush();
        fos.close();
    }

}
