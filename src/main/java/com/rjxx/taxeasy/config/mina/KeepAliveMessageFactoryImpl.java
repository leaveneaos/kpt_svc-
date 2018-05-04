package com.rjxx.taxeasy.config.mina;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;

/**
 * @ClassName KeepAliveMessageFactoryImpl
 * @Description TODO
 * @Author 许黎明
 * @Date 2018-05-04 14:10
 * @Version 1.0
 **/
public class KeepAliveMessageFactoryImpl implements KeepAliveMessageFactory {
    @Override
    public boolean isRequest(IoSession session, Object message) {
        return false;
    }

    @Override
    public boolean isResponse(IoSession session, Object message) {
        return true;
    }

    @Override
    public Object getRequest(IoSession session) {
        return null;
    }

    @Override
    public Object getResponse(IoSession session, Object request) {
        return null;
    }
}
