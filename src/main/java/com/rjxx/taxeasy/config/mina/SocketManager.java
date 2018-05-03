package com.rjxx.taxeasy.config.mina;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * @ClassName SocketManager
 * @Description* SocketManager
 * socket tcp 客户端
 * @Author 许黎明
 * @Date 2018-04-27 10:41
 * @Version 1.0
 **/
public class SocketManager {

    private static Logger logger = LoggerFactory.getLogger(SocketManager.class);

    /**
     * socket对象池
     */
    public static ObjectPool<Socket> op;

    public static String sendMessage( String message) {
        if (SocketManager.op == null) {
            createPool();
        }
        ObjectPool<Socket> op = SocketManager.op;
        Socket soc = null;
        boolean sucess = false;
        try {
            try {
                soc = (Socket)op.borrowObject();
                sendData(message, soc);
            }catch (Exception ie) {
                logger.error("-----socket发送数据失败-----", (Throwable)ie);
                if (soc != null) {
                    op.invalidateObject(soc);
                }
                soc = (Socket)op.borrowObject();
                sendData(message, soc);
            }
            String str;
            str = readData(soc);
            sucess = true;
            return str;
        }
        catch (Exception e) {
            if (soc != null) {
                try {
                    op.invalidateObject(soc);
                }catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            logger.error("----Socket发送数据失败---", (Throwable)e);
            return e.getMessage();
        }
        finally {
            if (sucess) {
                try {
                    op.returnObject(soc);
                }
                catch (Exception e2) {
                    logger.error("------Socket归还对象失败-----", (Throwable)e2);
                    e2.printStackTrace();
                }
            }
        }
    }

    public static String readData(Socket soc) throws IOException {
        logger.info("-----线程ID:" + Thread.currentThread().getId() + "----socket对象---" + soc + "----------");
        //输入流
        InputStream is=soc.getInputStream();
        BufferedReader br=new BufferedReader(new InputStreamReader(is));
        //接收服务器的相应
        String reply=null;
        while(!((reply=br.readLine())==null)){
            logger.info("接收服务器的信息："+reply);
        }
        return reply;
    }
    public static void sendData(String messsge,Socket soc) throws Exception {
        logger.info("-----线程ID:" + Thread.currentThread().getId() + "----socket对象---" + soc + "----------");
        final OutputStream os = soc.getOutputStream();
        PrintWriter pw=new PrintWriter(os);
        pw.write(messsge);
        pw.flush();
    }


    public static void createPool() {
        final GenericObjectPool.Config cfg = new GenericObjectPool.Config();
        cfg.maxActive = 500;
        cfg.maxIdle = -1;
        SocketManager.op = (ObjectPool<Socket>)new GenericObjectPool((PoolableObjectFactory)new PoolFactory(), cfg);
    }

}
