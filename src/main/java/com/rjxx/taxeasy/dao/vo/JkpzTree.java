package com.rjxx.taxeasy.dao.vo;

import java.util.List;

/**
 * @author: zsq
 * @date: 2018/3/28 19:36
 * @describe:
 */
public class JkpzTree {
    private String id;
    private String text;
    private String templateId;
    private List children;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public List getChildren() {
        return children;
    }

    public void setChildren(List children) {
        this.children = children;
    }
}
