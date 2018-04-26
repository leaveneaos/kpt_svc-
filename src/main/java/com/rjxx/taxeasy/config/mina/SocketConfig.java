package com.rjxx.taxeasy.config.mina;

import com.rjxx.taxeasy.config.password.PasswordConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.Socket;

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



    @PostConstruct
    public void initialize() throws Exception {
            try {
                //创建一个客户端socket
                Socket socket = new Socket(PasswordConfig.ip,PasswordConfig.port);
                SocketManger.getInstance().setSocket(socket);
                //向服务器端传递信息
            }catch (Exception e){
                e.printStackTrace();
            }
    }
}
