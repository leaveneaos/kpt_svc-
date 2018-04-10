package com.rjxx.taxeasy.bizhandle.pdf;

import com.rjxx.utils.ResourceLoader;
import freemarker.template.Configuration;

import java.io.File;
import java.io.IOException;


/**
 *@ClassName FreemarkerConfiguration
 *@Description freemark配置
 *@Author Him
 *@Date 2015-09-22.
 *@Version 1.0
 **/
public class FreemarkerConfiguration {
    private static Configuration config = null;

    /**
     * 获取 FreemarkerConfiguration
     *
     * @Title: getConfiguation
     * @Description:
     * @return
     * @author
     */
    public static synchronized Configuration getConfiguation() {
        if (config == null) {
            setConfiguation();
        }
        return config;
    }
    /**
     * 设置 配置
     * @Title: setConfiguation
     * @Description:
     * @author
     */
    private static void setConfiguation() {
        config = new Configuration();
        String path = ResourceLoader.getPath("");
        System.out.println("path="+path);
        try {
            config.setDirectoryForTemplateLoading(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        String path = ResourceLoader.getPath("config/fonts");
        System.out.println("path="+path);
    }
}
