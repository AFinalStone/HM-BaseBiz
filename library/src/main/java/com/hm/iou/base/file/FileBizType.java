package com.hm.iou.base.file;

/**
 * Created by syl on 2018/9/19.
 */

public enum FileBizType {

    Temporary(0, "临时文件"),
    Advertisement(1, ""),
    Activity(2, ""),
    AgencyTemplate(3, ""),
    AgencyOriginal(4, "平台借条原始图片"),
    AgencyComplete(5, "平台借条评论"),
    PaperBorrowTemplate(6, ""),
    PaperBorrowOriginal(7, "纸质借条"),
    PaperBorrowComplete(8, "纸质借条评论"),
    PaperRecvTemplate(9, ""),
    PaperRecvOriginal(10, "纸质收条"),
    PaperRecvComplete(11, "纸质收条评论"),
    FunTemplate(12, ""),
    FunComplete(13, "娱乐借条评论"),
    Help(14, ""),
    Law(15, ""),
    Share(16, ""),
    Signature(17, ""),
    Avatar(18, "头像"),
    IdPhoto(19, ""),
    Income(20, "收入"),
    Complain(21, ""),
    MoneyTemplate(22, ""),
    MoneyOriginal(23, "资金借条"),
    MoneyComplete(24, "资金借条评论"),
    FunOriginal(25, "娱乐借条"),
    FunIllustration(26, "娱乐借条插画"),
    DebtNote(27, "记债本"),
    IOUElecRecv(28, "电子收条"),
    IOUElecRecvAttachFile(29, "电子收条附件"),
    IOUElecRecvComplete(30, "电子收条评论"),
    CustomerPrivacyFile(100, "用户私密文件"),//ps: 该类型的会使用私密的bucket存储用户的文件
    IdCardFront(101, "身份证正面"),//ps: 该类型的会使用私密的bucket存储用户的文件
    IdCardBack(102, "身份证反面"),//ps: 该类型的会使用私密的bucket存储用户的文件
    LivenessPic(103, "用户活体图片");//ps: 该类型的会使用私密的bucket存储用户的文件

    int type;
    String name;

    FileBizType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
