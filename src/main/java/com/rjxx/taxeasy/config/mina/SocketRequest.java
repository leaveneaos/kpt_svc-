package com.rjxx.taxeasy.config.mina;

/**
 * @ClassName SocketRequest
 * @Description TODO
 * @Author 许黎明
 * @Date 2018-05-11 18:06
 * @Version 1.0
 **/
public class SocketRequest {

    private long startTime = System.currentTimeMillis();

    private Exception exception = null;

    private String commandId;

    private String returnMessage;

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public boolean timeout() {
        long now = System.currentTimeMillis();
        //请求超过5分钟了就超时
        if (now - startTime > 300 * 1000) {
            return true;
        } else {
            return false;
        }
    }
}
