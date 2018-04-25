package com.rjxx.taxeasy.config.mina;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 *@ClassName SocketService
 *@Description 连接凯盈平台mina 客户端配置
 *@Author 许黎明
 *@Date 2018/4/25.
 *@Version 1.0
 **/
@Component("SocketConfig")
public class SocketConfig {

    private static Logger logger = LoggerFactory.getLogger(SocketConfig.class);

    @Value("${socket.port}")
    private int port;

    @Value("${socket.server}")
    private String  ip;

    @PostConstruct
    public void initialize() throws IOException {

        /**
         * 创建一个NioSocketConnector 用于链接服务端
         */
        NioSocketConnector nioSocketConnector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
        /**
         * 设置IO处理器
         */
        nioSocketConnector.setHandler(new ServerHandler());

        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"));
        textLineCodecFactory.setDecoderMaxLineLength(Integer.MAX_VALUE);
        textLineCodecFactory.setEncoderMaxLineLength(Integer.MAX_VALUE);
        nioSocketConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(textLineCodecFactory));
        nioSocketConnector.getSessionConfig().setReadBufferSize(2048*10);
        nioSocketConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 120);
        // Create Session Configuration
        nioSocketConnector.getSessionConfig().setReuseAddress(true);
        logger.info("Starting Client......");
        //链接服务端
        ConnectFuture connectFuture =
                nioSocketConnector.connect(new InetSocketAddress(ip, port)) ;
        // Connect and be ready to listen
        logger.info("Server listening on " + port);
        //阻塞等待，知道链接服务器成功，或被中断
        connectFuture.awaitUninterruptibly() ;

        IoSession session = connectFuture.getSession() ;
        SocketSession.getInstance().setSession(session);
       /* //阻塞，知道session关闭
        session.getCloseFuture().awaitUninterruptibly() ;

        //关闭链接
        nioSocketConnector.dispose() ;*/
    }

}
