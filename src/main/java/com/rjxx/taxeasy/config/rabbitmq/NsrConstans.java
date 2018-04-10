package com.rjxx.taxeasy.config.rabbitmq;


/**
 *@ClassName PasswordConfig
 *@Description rabbitMQ配置类
 *@Author wangyahui
 *@Date 2018/3/6 0006.
 *@Version 1.0
 **/
public class NsrConstans {
    /**
     * 小规模纳税人
     */
    public static final String[] XGM_NSR ={"-1","0"};
    /**
     * 一般纳税人
     */
    public static final String[] YB_NSR ={"1","0"};
    /**
     * 简易征收一般纳税人
     */
    public static final String[] JYZS_NSR ={"-1","0"};
    /**
     * 部分简易征收一般纳税人
     */
    public static final String[] BFJYZS_NSR ={"-1","0","1"};
}
