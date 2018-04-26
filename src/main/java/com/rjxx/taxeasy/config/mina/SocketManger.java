package com.rjxx.taxeasy.config.mina;

import java.net.Socket;
import java.util.Date;

/**
 *@ClassName SocketService
 *@Description 连接凯盈平台mina 客户端Session配置
 *@Author 许黎明
 *@Date 2018/4/25.
 *@Version 1.0
 **/
public class SocketManger {


    /**
     * 静态内部类懒汉式单列模式
     */
    private static class SingletonSocketSession {
        private static final SocketManger INSTANCE = new SocketManger();
    }
    public static final SocketManger getInstance() {
        return SocketManger.SingletonSocketSession.INSTANCE;
    }

    private Date connectTime;

    private Socket socket;

    public Date getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
