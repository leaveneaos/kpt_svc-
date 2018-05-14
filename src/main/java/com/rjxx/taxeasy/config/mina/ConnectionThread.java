package com.rjxx.taxeasy.config.mina;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName ConnectionThread
 * @Description TODO
 * @Author 许黎明
 * @Date 2018-05-07 13:27
 * @Version 1.0
 **/
public class ConnectionThread extends Thread{

    private static Logger logger = LoggerFactory.getLogger(ConnectionThread.class);


    private NioSocketConnector connector;

    private boolean isConnection=false;



    public void setConnector(NioSocketConnector connector) {
        this.connector = connector;
    }

    @Override
    public void run(){
        for(;;){
            try {
                Thread.sleep(20*000);
                ConnectFuture future = connector.connect();
                // 等待连接创建成功
                future.awaitUninterruptibly();
                // 获取会话
                IoSession session = future.getSession();
                SocketSession.getInstance().setSession(session);
                if (session.isConnected()) {
                    logger.info("断线重连[" + connector.getDefaultRemoteAddress().getHostName() + ":" + connector.getDefaultRemoteAddress().getPort() + "]成功");
                    break;
                }
            }catch (Exception e){
                logger.info("重连服务器登录失败,3秒再连接一次:" + e.getMessage());
            }
        }
    }
}
