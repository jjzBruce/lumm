package com.lumm.toolbox.pdf;

import cn.hutool.core.io.IoUtil;
import org.junit.Test;

import java.io.*;

/**
 * Pdf工具
 *
 * @author <a href="mailto:brucezhang_jjz@163.com">zhangj</a>
 * @since 1.0.0
 */
public class PdfUtilTest {

    @Test
    public void test() throws FileNotFoundException {
        generator("overseaAssistance.html", "aa");
    }

    @Test
    public void test_spanPage() throws FileNotFoundException {
        generator("overseaAssistance_spanpage.html", "跨页测试-" + System.currentTimeMillis());
        generator("overseaAssistance_spanpage_1.html", "跨页优化测试-" + System.currentTimeMillis());
    }

    public void generator(String templateName, String outName) throws FileNotFoundException {
        long start = System.currentTimeMillis();
        // 模板数据
        OverseaVo overseaVo = new OverseaVo();

        overseaVo.setPolicyNo("1234567890123456");
        overseaVo.setHolderName("丽丽张123丽丽张123");
        overseaVo.setInsuredName("丽丽张123丽丽张123丽丽张123丽丽张123");
        overseaVo.setBeneficiaryName("测试受益人姓名");
        overseaVo.setBranchName("北京");
        overseaVo.setCompanyName("科索沃公司");
        overseaVo.setDestination("英国,俄罗斯,冰岛,日内瓦,威尼斯小镇");
        overseaVo.setHolderAdress("北京市屋顶后街金融大街14号中国人寿广场xxx曾x101室");
        overseaVo.setHolderPostCode("123456");
        overseaVo.setInsuredBirthday("2013-11-10");
        overseaVo.setInsuredIDNo("123456789012345678");
        overseaVo.setInsuredName("爱新觉罗启星");
        overseaVo.setInsuredPassportNo("测试护照号码123456789");
        overseaVo.setInsuredPhone("13112345678");
        overseaVo.setInsuredPingyinName("aixinjuluoqixing");
        overseaVo.setInsuredSex("女");
        overseaVo.setIssueDate("2013-11-12");
        overseaVo.setPeriod("十一年");
        overseaVo.setPremium("1009.00");
        overseaVo.setRelation("子女");
        overseaVo.setRemarks("这是一张测试保单,仅为测试,学习所用,请勿转载");
        overseaVo.setAccidentalSumInsured("150000");
        overseaVo.setEmergencySumInsured("500000");
        overseaVo.setMedicalSumInsured("220000");

        // classpath 中模板路径
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(templateName);
        // 生成pdf路径
        String outputFile = "D:\\download\\" + outName + ".pdf";
        OutputStream os = new FileOutputStream(outputFile);
        // 生成pdf
        PdfUtil.htmlTemplate2Pdf("template", resourceAsStream, overseaVo, os);
        IoUtil.close(os);
        System.err.println(" \n pdf生成成功  IS OK path=\n" + outputFile);
        System.err.println("耗时time=" + (System.currentTimeMillis() - start) / 1000);

    }


}
