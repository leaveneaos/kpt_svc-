package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Dzfplog;
import com.rjxx.taxeasy.dao.orm.DzfplogJpaDao;
import com.rjxx.taxeasy.dao.orm.DzfplogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Nov 01 17:20:13 CST 2016
 *
 * @author Wangyong
 */ 
@Service
public class DzfplogService {

    @Autowired
    private DzfplogJpaDao dzfplogJpaDao;

    @Autowired
    private DzfplogMapper dzfplogMapper;

    public Dzfplog findOne(int id) {
        return dzfplogJpaDao.findOne(id);
    }

    public void save(Dzfplog dzfplog) {
        dzfplogJpaDao.save(dzfplog);
    }

    public void save(List<Dzfplog> dzfplogList) {
        dzfplogJpaDao.save(dzfplogList);
    }

    public Dzfplog findOneByParams(Map params) {
        return dzfplogMapper.findOneByParams(params);
    }

    public List<Dzfplog> findAllByParams(Map params) {
        return dzfplogMapper.findAllByParams(params);
    }

    public List<Dzfplog> findByPage(Pagination pagination) {
        return dzfplogMapper.findByPage(pagination);
    }

}

