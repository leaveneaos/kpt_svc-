package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Csb;
import com.rjxx.taxeasy.dao.orm.CsbJpaDao;
import com.rjxx.taxeasy.dao.orm.CsbMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Wed Nov 02 11:58:41 CST 2016
 *
 * @author ZhangBing
 */ 
@Service
public class CsbService {

    @Autowired
    private CsbJpaDao csbJpaDao;

    @Autowired
    private CsbMapper csbMapper;

    public Csb findOne(int id) {
        return csbJpaDao.findOne(id);
    }

    public void save(Csb csb) {
        csbJpaDao.save(csb);
    }

    public void save(List<Csb> csbList) {
        csbJpaDao.save(csbList);
    }

    public Csb findOneByParams(Map params) {
        return csbMapper.findOneByParams(params);
    }

    public List<Csb> findAllByParams(Map params) {
        return csbMapper.findAllByParams(params);
    }

    public List<Csb> findByPage(Pagination pagination) {
        return csbMapper.findByPage(pagination);
    }

}

