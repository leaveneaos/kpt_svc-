package com.rjxx.taxeasy.config.mina;

import com.rjxx.comm.utils.ApplicationContextUtils;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 *@ClassName SocketService
 *@Description 连接凯盈平台mina 客户端业务处理
 *@Author 许黎明
 *@Date 2018/4/25.
 *@Version 1.0
 **/
public class ServerHandler extends IoHandlerAdapter {



    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);




    /**
     * 线程池执行任务
     */
    private static ThreadPoolTaskExecutor taskExecutor = null;






    /**
     * 发送消息
     * @param message
     */
    public static void sendMessage( String message) {
        IoSession session=SocketSession.getInstance().getSession();
        sendMessage(session,message);
    }


    /**
     * 发送消息
     *
     * @param session
     * @param message
     */
    public static void sendMessage(IoSession session, String message) {
        session.write(message);
    }

    @Override
    public void sessionOpened(IoSession session) throws Exception {
    }

    @Override
    public void sessionClosed(IoSession session) throws Exception {

    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        ReceiveTask receiveTask = new ReceiveTask();
        receiveTask.setMsg((String)message);
        receiveTask.setSession(session);
        if (taskExecutor == null) {
            taskExecutor = ApplicationContextUtils.getBean(ThreadPoolTaskExecutor.class);
        }
        taskExecutor.execute(receiveTask);
    }

    @Override
    public void messageSent(IoSession session, Object message) throws Exception {
       logger.info("客户端发送信息成功  message:" + message);
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        String kpdid = (String) session.getAttribute("kpdid");
        if (kpdid != null) {
            logger.info("kpd:" + kpdid + " " + session + " idle time out!!!");
        }
        logger.info("Client will interrupt the connection with the client");
        session.closeNow();
    }

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        String kpdid = (String) session.getAttribute("kpdid");
        if (kpdid != null) {
            logger.error("kpd:" + kpdid + " " + session + " exception caught!!!", cause);
        }
    }

    /**
     * 接收任务
     */
    class ReceiveTask implements Runnable {

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        private String msg;
        private IoSession session;

        @Override
        public void run() {
            logger.info("-------消息---------"+msg);
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public void setSession(IoSession session) {
            this.session = session;
        }
    }

}
