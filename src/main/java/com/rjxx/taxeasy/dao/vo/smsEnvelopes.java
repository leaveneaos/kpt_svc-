package com.rjxx.taxeasy.dao.vo;

/**
 * Created by xlm on 2017/9/19.
 */
public class smsEnvelopes {

    private  String toPhoneNumber;

    private  messageParams messageParams;

    private  String messageType;

    public String getToPhoneNumber() {
        return toPhoneNumber;
    }

    public void setToPhoneNumber(String toPhoneNumber) {
        this.toPhoneNumber = toPhoneNumber;
    }

    public messageParams getMessageParams() {
        return messageParams;
    }

    public void setMessageParams(messageParams messageParams) {
        this.messageParams = messageParams;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
}
