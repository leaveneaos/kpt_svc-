package com.rjxx.taxeasy.dao.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *@ClassName ReturnInvoiceFile
 *@Description TODO
 *@Author Administrator
 *@Date 2017-04-12.
 *@Version 1.0
 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ReturnInvoiceFile")
public class ReturnInvoiceFile {

    @XmlElement(name = "ReturnCode")
    private String returnCode;

    @XmlElement(name = "SerialNumber")
    private String lsh;

    @XmlElement(name = "FileContent")
    private String fileContent;

    /**
     * 批量导入结果标志
     */
    @XmlElement(name = "BulkImportResultFlag")
    private boolean bulkImportResultFlag = true;

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getLsh() {
        return lsh;
    }

    public void setLsh(String lsh) {
        this.lsh = lsh;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
    }

    public boolean isBulkImportResultFlag() {
        return bulkImportResultFlag;
    }

    public void setBulkImportResultFlag(boolean bulkImportResultFlag) {
        this.bulkImportResultFlag = bulkImportResultFlag;
    }
}
