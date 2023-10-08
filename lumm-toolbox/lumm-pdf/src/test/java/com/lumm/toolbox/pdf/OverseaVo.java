package com.lumm.toolbox.pdf;


import lombok.Data;

/**
 * 境外救援pdf数据VO
 */
@Data
public class OverseaVo {

    /**
     * 保单号
     */
    private String policyNo;
    /**
     * 投保人姓名
     */
    private String holderName;
    /**
     * 投被保人关系
     */
    private String relation;
    /**
     * 投保人联络地址
     */
    private String holderAdress;
    /**
     * 投保人邮编
     */
    private String holderPostCode;

    /**
     * 被保险人姓名
     */
    private String insuredName;
    /**
     * 被保险人姓名拼音
     */
    private String insuredPingyinName;
    /**
     * 被保险人护照号码
     */
    private String insuredPassportNo;
    /**
     * 被保险人性别
     */
    private String insuredSex;
    /**
     * 被保险人出生日期
     */
    private String insuredBirthday;
    /**
     * 被保险人电话
     */
    private String insuredPhone;

    /**
     * 被保险人证件号码
     */
    private String insuredIDNo;

    /**
     * 前往国家或地区
     */
    private String destination;

    /**
     * 受益人姓名
     */
    private String beneficiaryName;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 保险期间
     */
    private String period;

    /**
     * 境外意外伤害或残疾保额
     */
    private String accidentalSumInsured;

    /**
     * 紧急救援医疗保额
     */
    private String emergencySumInsured;

    /**
     * 附加境外紧急救援医保额
     */
    private String medicalSumInsured;

    /**
     * 总保费
     */
    private String premium;
    /**
     * 签发日期
     */
    private String issueDate;

    /**
     * 省份
     */
    private String branchName;

    /**
     * 合作公司名称
     */
    private String companyName;

    /**
     * 图片存放路径
     */
    private String imagePath;

    public String findPrimaryKey() {
        return this.policyNo;
    }

}
