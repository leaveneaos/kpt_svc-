package com.rjxx.taxeasy.bizhandle.maindata.command;


/**
 * @ClassName SendCommand
 * @Description TODO
 * @Author 许黎明
 * @Date 2018/4/10 15:58
 * @Version 1.0
 **/
public enum SendCommand {

    //开具发票，重新打印，设置加密的key，踢出登录，获取发票代码发票号码,作废发票，重打发票，发送待开票数据, 获取库存
    Invoice, SetDesKey, Logout, GetCodeAndNo, VoidInvoice, ReprintInvoice, SendPendingData, GetKc,
    UploadFile,ViewFileInfo,DownloadFile,BoxInvoice
}
