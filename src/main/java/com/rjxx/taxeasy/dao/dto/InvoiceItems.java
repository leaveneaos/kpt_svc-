package com.rjxx.taxeasy.dao.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by xlm on 2017/6/9.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class InvoiceItems {

    @XmlAttribute
    private int count;

    private List<InvoiceItem> InvoiceItem;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<InvoiceItem> getInvoiceItem() {
        return InvoiceItem;
    }

    public void setInvoiceItem(List<InvoiceItem> invoiceItem) {
        InvoiceItem = invoiceItem;
    }
}
