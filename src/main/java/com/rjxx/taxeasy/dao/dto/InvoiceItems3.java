package com.rjxx.taxeasy.dao.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by xlm on 2017/8/29.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "InvoiceItems")
public class InvoiceItems3 {

    @XmlAttribute
    private int count;

    private List<InvoiceItem3> InvoiceItem;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<InvoiceItem3> getInvoiceItem() {
        return InvoiceItem;
    }

    public void setInvoiceItem(List<InvoiceItem3> invoiceItem) {
        InvoiceItem = invoiceItem;
    }
}
