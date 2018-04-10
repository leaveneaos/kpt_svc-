package com.rjxx.taxeasy.dao.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by xlm on 2017/6/9.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Request")
public class ReturnData {

    private  OperationItem OperationItem;

    private  InvoiceItems  InvoiceItems;

    public OperationItem getOperationItem() {
        return OperationItem;
    }

    public void setOperationItem(OperationItem operationItem) {
        OperationItem = operationItem;
    }

    public InvoiceItems getInvoiceItems() {
        return InvoiceItems;
    }

    public void setInvoiceItems(InvoiceItems invoiceItems) {
        InvoiceItems = invoiceItems;
    }
}
