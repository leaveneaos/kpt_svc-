package com.rjxx.taxeasy.config.mina;

import org.apache.mina.core.session.IoSession;

import java.util.Date;

/**
 *@ClassName SocketService
 *@Description 连接凯盈平台mina 客户端Session配置
 *@Author 许黎明
 *@Date 2018/4/25.
 *@Version 1.0
 **/
public class SocketSession {


    /**
     * 静态内部类懒汉式单列模式
     */
    private static class SingletonSocketSession {
        private static final SocketSession INSTANCE = new SocketSession();
    }
    public static final SocketSession getInstance() {
        return SocketSession.SingletonSocketSession.INSTANCE;
    }

    private Date connectTime;

    private IoSession session;

    public Date getConnectTime() {
        return connectTime;
    }

    public void setConnectTime(Date connectTime) {
        this.connectTime = connectTime;
    }

    public IoSession getSession() {
        return session;
    }

    public void setSession(IoSession session) {
        this.session = session;
    }
}
