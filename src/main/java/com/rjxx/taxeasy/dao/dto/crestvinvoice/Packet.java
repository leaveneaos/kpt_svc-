package com.rjxx.taxeasy.dao.dto.crestvinvoice;

import java.util.List;

/**
 * @ClassName Packet
 * @Description 凯盈Q200命令方式报文封装
 * @Author 许黎明
 * @Date 2018-04-24 13:38
 * @Version 1.0
 **/
public class Packet {


    /**
     * 静态内部类懒汉式单列模式
     */
    private static class SingletonHolder {
        private static final Packet INSTANCE = new Packet();
    }
    public static final Packet getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 请求层
     */
    public class RequestLayer {
        /**
         * 应用 ID
         */
        public  String AppID;
        /**
         * 请求类型
         */
        public String ReqType;
        /**
         * 业务层密文
         */
        public String ReqData;

    }

    /**
     * 终端授权
     */
    public class DeviceAuth{
        /**
         * 请求类型
         */
        public String ReqType="DeviceAuth";
        /**
         * 终端 SN
         */
        public String DeviceSN;
        /**
         * 终端注册密码
         */
        public String DevicePassword;


    }

    /**
     * 查询终端状态
     */
    public class DeviceState{

        /**
         * 请求类型
         */
        public String ReqType;
        /**
         * 终端 SN
         */
        public String DeviceSN;
        /**
         * 终端授权密钥
         */
        public String DeviceKey;

    }
    /**
     * 终端指令层
     */
    public class Terminal {

        /**
         *协议版本
         */
        public int ProtocolVer=1;
        /**
         * 流水号
         */
        public String SeqNumber;
        /**
         * 压缩方式
         */
        public int ZipType;
        /**
         * 加密方式
         */
        public int  EncryptType;
        /**
         * 终端SN
         */
        public String DeviceSN;
        /**
         * 用户 ID
         */
        public String UserID;
        /**
         * 业务类型
         */
        public String OpType;
        /**
         * 业务内容
         */
        public String Data;


    }

    /**
     * 设置税控装置密码
     */
    public class InputUDiskPassword {

        /**
         * 税控装置编号
         */
        public String UDiskSn;
        /**
         * 税控装置密码
         */
        public String UDiskPwd;
        /**
         * CA 密码
         */
        public String CaPwd;
        /**
         * 报税盘密码
         */
        public String TaxDiskPwd;
    }

    /**
     * 写入配置
     */
    public class SetConfig{
        /**
         * 销方地址
         */
        public String MerchantAddress;
        /**
         * 销方电话
         */
        public String MerchantPhone;
        /**
         * 销方银行名称
         */
        public String MerchantBank;
        /**
         * 销方银行账号
         */
        public String MerchantBankAccount;
    }

    /**
     * 开票请求
     */
    public class NewInvoice{
        /**
         * 开票类型
         */
        public String IssueType;
        /**
         * 发票种类
         */
        public String InvoiceType;
        /**
         *销方地址
         */
        public String MerchantAddress;
        /**
         * 销方电话
         */
        public String MerchantPhone;
        /**
         * 销方银行名称
         */
        public String MerchantBank;
        /**
         * 销方银行账号
         */
        public String MerchantBankAccount;
        /**
         * 购方纳税人识别号
         */
        public String PurchaserTaxID;
        /**
         * 购方名称
         */
        public String PurchaserName;
        /**
         * 购方地址
         */
        public String PurchaserAddr;
        /**
         * 购方电话
         */
        public String PurchaserPhone;
        /**
         * 购方银行名称
         */
        public String PurchaserBank;
        /**
         *  购方银行账号
         */
        public String PurchaserAccount;
        /**
         *  购方邮箱
         */
        public String PurchaserEmail;
        /**
         *  购方手机号码
         */
        public String PurchaserMobile;
        /**
         *  合计金额(不含税)
         */
        public String TotalAmountTaxExcluded;
        /**
         *  合计税额
         */
        public String TotalTax;
        /**
         *   价税合计
         */
        public String TotalAmount;
        /**
         *    备注
         */
        public String Remarks;
        /**
         *    收款人
         */
        public String Payee;
        /**
         *    复核人
         */
        public String Reviewer;
        /**
         *  开票人
         */
        public String Drawee;
        /**
         *  清单标志
         */
        public String IsPrintList;
        /**
         *  原发票代码
         */
        public String OriginInvoiceCode;
        /**
         *  原发票号码
         */
        public String OriginInvoiceNum;
        /**
         *  通知单编号
         */
        public String NegSpecialInvoicePermitNum;
        /**
         *  商品分类版本号
         */
        public String CatalogVer;
        /**
         *  商品明细项
         */
        public List ItemList;
    }

    /**
     * 商品明细列表
     */
    public class Item{

        /**
         *  发票行性质
         */
        public String EntryType;
        /**
         *  商品名称
         */
        public String ItemName;
        /**
         *  含税单价
         */
        public String UnitPrice;
        /**
         *  单价(不含税)
         */
        public String UnitPriceTaxExcluded;
        /**
         *  含税金额
         */
        public String Amount;
        /**
         *  金额(不含税)
         */
        public String AmountTaxExcluded;
        /**
         *   数量
         */
        public String Quantity;
        /**
         *   税率
         */
        public String TaxRate;

        /**
         *  税额
         */
        public String Tax;
        /**
         *   规格型号
         */
        public String Specifications;
        /**
         *    单位
         */
        public String Unit;
        /**
         *    差额征收扣除额
         */
        public String TaxExcludingAmount;
        /**
         *    商品分类名称
         */
        public String CatalogName;
        /**
         *   商品分类简称
         */
        public String CatalogShortName;
        /**
         *   商品分类编码
         */
        public String CatalogCode;
        /**
         *   优惠政策标识
         */
        public String HasPreferentialPolicy;
        /**
         *  优惠政策类型
         */
        public String PreferentialPolicy;
        /**
         *  免税类型
         */
        public String TaxFreeType;

    }

    /**
     * 查询发票
     */
    public class InvoiceQuery{
        /**
         *  查询类型
         */
        public String QueryType;
        /**
         *  发票种类
         */
        public String InvoiceType;
        /**
         *  查询方式
         */
        public String QueryBy;
        /**
         *  发票代码
         */
        public String InvoiceCode;
        /**
         *  查询起始号码
         */
        public String InvoiceNumStart;
        /**
         *  查询截止号码
         */
        public String InvoiceNumEnd;
        /**
         *  查询起始日期
         */
        public String DateStart;
        /**
         *   查询截止日期
         */
        public String DateEnd;
    }

    /**
     * 作废发票
     */
    public class InvalidateInvoice{
        /**
         *  作废类型
         */
        public String InvalidateType;
        /**
         *  发票种类
         */
        public String InvoiceType;
        /**
         *  发票代码
         */
        public String InvoiceCode;
        /**
         *  发票号码
         */
        public String InvoiceNum;
        /**
         *  作废人
         */
        public String Operator;

    }

    /**
     * 获取当前发票信息
     */
    public class GetCurrentInvoiceInfo {

        /**
         *  发票种类
         */
        public String InvoiceType;

    }

    /**
     *  获取发票段信息
     */
    public class GetAllInvoiceSections{
        /**
         *  税控装置编号
         */
        public String UDiskNum;
        /**
         *  发票种类
         */
        public String InvoiceType;
    }

    /**
     * 发票分发回收
     */
    public class InvoiceDistribute{
        /**
         *  分发类型
         */
        public String DistributeType;
        /**
         *  源税控装置编号
         */
        public String SrcUDiskNum;
        /**
         *  目标税控装置编号
         */
        public String DstUDiskNum;
        /**
         *   发票种类
         */
        public String InvoiceType;
        /**
         *   发票代码
         */
        public String InvoiceCode;
        /**
         *  起始发票号码
         */
        public String StartInvoiceNum;
        /**
         *  发票份数
         */
        public String InvoiceQuantity;
    }
}
