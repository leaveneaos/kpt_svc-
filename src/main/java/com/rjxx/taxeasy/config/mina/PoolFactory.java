package com.rjxx.taxeasy.config.mina;

import com.rjxx.taxeasy.config.password.PasswordConfig;
import org.apache.commons.pool.PoolableObjectFactory;

import java.io.IOException;
import java.net.Socket;

/**
 * @ClassName PoolFactory
 * @Description 继承对象池工厂
 * @Author 许黎明
 * @Date 2018-05-03 17:38
 * @Version 1.0
 **/
public class PoolFactory implements PoolableObjectFactory<Socket> {

    /**
     * 初始化对象
     * @return
     * @throws Exception
     */
    @Override
    public Socket makeObject() throws Exception {
        Socket socket = new Socket(PasswordConfig.ip, PasswordConfig.port);
        socket.setSoTimeout(0);
        socket.setKeepAlive(true);
        //设置socket发包缓冲为32k；
        socket.setSendBufferSize(32*1024);
        //设置socket底层接收缓冲为32k
        socket.setReceiveBufferSize(32*1024);
        //关闭Nagle算法.立即发包
        socket.setTcpNoDelay(true);
        //socket.
        return socket;
    }

    /**
     * 销毁对象
     * @param obj
     * @throws Exception
     */
    @Override
    public void destroyObject(Socket obj) throws Exception {
        try {
            obj.getOutputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        try {
            obj.getInputStream();
        } catch (IOException ex2) {
            ex2.printStackTrace();
        }
        try {
            obj.close();
        } catch (IOException ex3) {
            ex3.printStackTrace();
        }
    }

    /**
     * 校验对象是否仍然有效，已失效的对象会被自动交给destroyObject方法销毁
     * @param obj
     * @return
     */
    @Override
    public boolean validateObject(Socket obj) {
         return !obj.isInputShutdown() && !obj.isOutputShutdown() && !obj.isClosed();
    }

    /**
     * 这个方法用于将对象“激活”设置为适合开始使用的状态。
     * @param obj
     * @throws Exception
     */
    @Override
    public void activateObject(Socket obj) throws Exception {

    }

    /**
     * 用于将对象“挂起”设置为适合开始休眠的状态。
     * @param obj
     * @throws Exception
     */
    @Override
    public void passivateObject(Socket obj) throws Exception {

    }
}
