package com.rjxx.taxeasy.dao.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *@ClassName LoginInfo
 *@Description TODO
 *@Author Administrator
 *@Date 2017/1/12.
 *@Version 1.0
 **/
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LoginInfo")
public class LoginInfo {

    @XmlElement(name = "Kpdid")
    public String Kpdid;

    @XmlElement(name = "SessionId")
    public String SessionId;

    @XmlElement(name = "MacAddr")
    public String MacAddr;

}
