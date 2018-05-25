package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.orm.CrestvbusinessJpaDao;
import com.rjxx.taxeasy.dao.orm.CrestvbusinessMapper;
import com.rjxx.taxeasy.dao.bo.Crestvbusiness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Fri May 25 14:58:46 CST 2018
 *
 * @administrator
 */ 
@Service
public class CrestvbusinessService {

    @Autowired
    private CrestvbusinessJpaDao crestvbusinessJpaDao;

    @Autowired
    private CrestvbusinessMapper crestvbusinessMapper;

    public Crestvbusiness findOne(int id) {
        return crestvbusinessJpaDao.findOne(id);
    }

    public void save(Crestvbusiness crestvbusiness) {
        crestvbusinessJpaDao.save(crestvbusiness);
    }

    public void save(List<Crestvbusiness> crestvbusinessList) {
        crestvbusinessJpaDao.save(crestvbusinessList);
    }

    public Crestvbusiness findOneByParams(Map params) {
        return crestvbusinessMapper.findOneByParams(params);
    }

    public List<Crestvbusiness> findAllByParams(Map params) {
        return crestvbusinessMapper.findAllByParams(params);
    }

    public List<Crestvbusiness> findByPage(Pagination pagination) {
        return crestvbusinessMapper.findByPage(pagination);
    }

    public void delete(Crestvbusiness crestvbusiness) {
        crestvbusinessJpaDao.delete(crestvbusiness);
    }
}

