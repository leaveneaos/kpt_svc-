package com.rjxx.taxeasy.config.mina;

import org.apache.mina.core.service.IoService;
import org.apache.mina.core.service.IoServiceListener;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName IoListener
 * @Description TODO
 * @Author 许黎明
 * @Date 2018-05-07 11:53
 * @Version 1.0
 **/
public class IoListener implements IoServiceListener {

    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Override
    public void serviceActivated(IoService service) throws Exception {

        logger.info("#######IoListener监听:serviceActivated####");
    }

    @Override
    public void serviceIdle(IoService service, IdleStatus idleStatus) throws Exception {
        logger.info("#######IoListener监听:serviceIdle####");
    }

    @Override
    public void serviceDeactivated(IoService service) throws Exception {
        logger.info("#######IoListener监听:serviceDeactivated####");
    }

    @Override
    public void sessionCreated(IoSession session) throws Exception {
        logger.info("#######IoListener监听:sessionCreated####");
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {
        logger.info("#######IoListener监听:sessionClosed####");
    }

    @Override
    public void sessionDestroyed(IoSession session) throws Exception {
        logger.info("#######IoListener监听:sessionDestroyed####");
    }
}
