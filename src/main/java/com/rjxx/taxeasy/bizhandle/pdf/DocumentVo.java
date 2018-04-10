package com.rjxx.taxeasy.bizhandle.pdf;

import java.util.Map;


/**
 *@ClassName DocumentVo
 *@Description 模板中需要的数据视图 抽象类
 *@Author Him
 *@Date 2015-09-22.
 *@Version 1.0
 **/
public interface DocumentVo {
    /**
     * 获取主键,用于记录日志
     * @Title: findPrimaryKey
     * @Description: 获取主键,用于记录日志
     * @return
     * @author
     */
    public String findPrimaryKey();
    /**
     * 获取数据map
     * @Title: fillDataMap
     * @Description:
     * @return
     * @author
     */
    public Map<String, Object> fillDataMap();
}
