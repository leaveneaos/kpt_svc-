package com.rjxx.taxeasy.config.mina;

import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
        byte[] buf = new byte[4096];
        int len;
        String result=null;
        while ((len = is.read(buf)) != -1) {
            //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
            result=new String(buf, 0, len,"UTF-8");
        }
        return result;
    }
    private static boolean checkEnd(final byte[] dst) {
        final int pos = dst.length - 1;
        return dst[pos] == 62 && dst[pos - 1] == 115 && dst[pos - 2] == 115 && dst[pos - 3] == 101 && dst[pos - 4] == 110 && dst[pos - 5] == 105 && dst[pos - 6] == 115 && dst[pos - 7] == 117 && dst[pos - 8] == 98 && dst[pos - 9] == 47 && dst[pos - 10] == 60;
    }
    public static void sendData(String messsge,Socket soc) throws Exception {
        logger.info("-----线程ID:" + Thread.currentThread().getId() + "----socket对象---" + soc + "----------");
        OutputStream os = soc.getOutputStream();
        byte[] ba= messsge.getBytes("utf-8");
        os.write(ba);
        os.flush();
    }
    public static byte[] integerToBytes(int integer, int len) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        for (int i = 0; i < len; i ++) {
            bo.write(integer);
            integer = integer >> 8;
        }
        return bo.toByteArray();
    }

    public static void createPool() {
        GenericObjectPool.Config cfg = new GenericObjectPool.Config();
        cfg.maxActive = 500;
        cfg.maxIdle = -1;
        SocketManager.op = (ObjectPool<Socket>)new GenericObjectPool((PoolableObjectFactory)new PoolFactory(), cfg);
    }

}
