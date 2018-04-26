package com.rjxx.taxeasy.config.mina;

import com.rjxx.utils.StringUtils;
import org.apache.mina.core.RuntimeIoException;
import org.apache.mina.core.filterchain.IoFilterAdapter;
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
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    @Value("${CRESTV.port}")
    private int port;

    @Value("${CRESTV.server}")
    private String  ip;

    @PostConstruct
    public void initialize() throws Exception {

        /**
         * 创建一个NioSocketConnector 用于链接服务端
         */
        NioSocketConnector nioSocketConnector = new NioSocketConnector(Runtime.getRuntime().availableProcessors() + 1);
        /**
         * 设置IO处理器
         */
        nioSocketConnector.setHandler(new ServerHandler());
        nioSocketConnector.getFilterChain().addFirst("reconnection", new IoFilterAdapter() {
            @Override
            public void sessionClosed(NextFilter nextFilter, IoSession ioSession) throws Exception {
                for (; ; ) {
                    try {
                        Thread.sleep(2000);
                        ConnectFuture future = nioSocketConnector.connect();
                        // 等待连接创建成功
                        future.awaitUninterruptibly();
                        // 获取会话
                        ioSession = future.getSession();
                        SocketSession.getInstance().setSession(ioSession);
                        if (ioSession.isConnected()) {
                            logger.info("断线重连[" + nioSocketConnector.getDefaultRemoteAddress().getHostName() + ":" + nioSocketConnector.getDefaultRemoteAddress().getPort() + "]成功");
                            break;
                        }
                    } catch (Exception ex) {
                        logger.info("重连服务器登录失败,3秒再连接一次:" + ex.getMessage());
                    }
                }
            }
        });
        TextLineCodecFactory textLineCodecFactory = new TextLineCodecFactory(Charset.forName("UTF-8"), new String(StringUtils.hexString2Bytes("1A"), "utf-8"), new String(StringUtils.hexString2Bytes("1A"), "utf-8"));
        textLineCodecFactory.setDecoderMaxLineLength(Integer.MAX_VALUE);
        textLineCodecFactory.setEncoderMaxLineLength(Integer.MAX_VALUE);
        nioSocketConnector.getFilterChain().addLast("codec", new ProtocolCodecFilter(textLineCodecFactory));
        nioSocketConnector.getSessionConfig().setReadBufferSize(2048 * 10);
        nioSocketConnector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 120);
        // Create Session Configuration
        nioSocketConnector.getSessionConfig().setReuseAddress(true);
        logger.info("Starting Client......" + "------ip----" + ip + "---port---" + port);
        //链接服务端
        // 设置默认访问地址
        nioSocketConnector.setDefaultRemoteAddress(new InetSocketAddress(ip, port));
        for (; ; ) {
            try {
                ConnectFuture future = nioSocketConnector.connect();
                // 等待连接创建成功
                future.awaitUninterruptibly();
                // 获取会话
                IoSession session = future.getSession();
                SocketSession.getInstance().setSession(session);
                logger.error("连接服务端" + ip + ":" + port + "[成功]" + ",,时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                break;
            } catch (RuntimeIoException e) {
                logger.error("连接服务端" + ip + ":" + port + "失败" + ",,时间:" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + ", 连接MSG异常,请检查MSG端口、IP是否正确,MSG服务是否启动,异常内容:" + e.getMessage(), e);
                // 连接失败后,重连间隔2s
                Thread.sleep(2000);
            }
        }
    }
}
