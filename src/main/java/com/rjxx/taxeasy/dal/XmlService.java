package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.XmlBean;
import com.rjxx.taxeasy.dao.orm.XmlJpaDao;
import com.rjxx.taxeasy.dao.orm.XmlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Mon Dec 19 17:55:30 CST 2016
 *
 * @author ZhangBing
 */ 
@Service
public class XmlService {

    @Autowired
    private XmlJpaDao xmlJpaDao;

    @Autowired
    private XmlMapper xmlMapper;

    public XmlBean findOne(int id) {
        return xmlJpaDao.findOne(id);
    }

    public void save(XmlBean xml) {
        xmlJpaDao.save(xml);
    }

    public void save(List<XmlBean> xmlList) {
        xmlJpaDao.save(xmlList);
    }

    public XmlBean findOneByParams(Map params) {
        return xmlMapper.findOneByParams(params);
    }

    public List<XmlBean> findAllByParams(Map params) {
        return xmlMapper.findAllByParams(params);
    }

    public List<XmlBean> findByPage(Pagination pagination) {
        return xmlMapper.findByPage(pagination);
    }

}

