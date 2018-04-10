package com.rjxx.taxeasy.bizhandle.utils;

import com.rjxx.taxeasy.dao.dto.InvoiceResponse;


/**
 *@ClassName InvoiceResponseUtils
 *@Description TODO
 *@Author Administrator
 *@Date 2017-02-16.
 *@Version 1.0
 **/
public class InvoiceResponseUtils {

    public static InvoiceResponse responseError(String errorMessage){
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setReturnCode("9999");
        invoiceResponse.setReturnMessage(errorMessage);
        return invoiceResponse;
    }

    public static InvoiceResponse responseSuccess(String message){
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setReturnCode("0000");
        invoiceResponse.setReturnMessage(message);
        return invoiceResponse;
    }

    public static InvoiceResponse responseSuccess(){
        InvoiceResponse invoiceResponse = new InvoiceResponse();
        invoiceResponse.setReturnCode("9999");
        invoiceResponse.setReturnMessage("成功");
        return invoiceResponse;
    }
}
