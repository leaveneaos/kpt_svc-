package com.rjxx.taxeasy.config.mina;

import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;

/**
 * @ClassName IoListener
 * @Description TODO
 * @Author 许黎明
 * @Date 2018-05-07 11:53
 * @Version 1.0
 **/
public class IoListener implements IoServiceListener {
    @Override
    public void serviceActivated(IoService service) throws Exception {

    }

    @Override
    public void serviceIdle(IoService service, IdleStatus idleStatus) throws Exception {

    }

    @Override
    public void serviceDeactivated(IoService service) throws Exception {

    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {

    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {

    }

    @Override
    public void sessionDestroyed(IoSession session) throws Exception {

    }
}
