package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.orm.KpcfJpaDao;
import com.rjxx.taxeasy.dao.orm.KpcfMapper;
import com.rjxx.taxeasy.dao.bo.Kpcf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Aug 07 20:20:43 CST 2018
 *
 * @administrator
 */ 
@Service
public class KpcfService {

    @Autowired
    private KpcfJpaDao kpcfJpaDao;

    @Autowired
    private KpcfMapper kpcfMapper;

    public Kpcf findOne(int id) {
        return kpcfJpaDao.findOne(id);
    }

    public void save(Kpcf kpcf) {
        kpcfJpaDao.save(kpcf);
    }

    public void save(List<Kpcf> kpcfList) {
        kpcfJpaDao.save(kpcfList);
    }

    public Kpcf findOneByParams(Map params) {
        return kpcfMapper.findOneByParams(params);
    }

    public List<Kpcf> findAllByParams(Map params) {
        return kpcfMapper.findAllByParams(params);
    }

    public List<Kpcf> findByPage(Pagination pagination) {
        return kpcfMapper.findByPage(pagination);
    }

    public void deleteById(int id){kpcfMapper.deleteById(id);}

    public List<Kpcf> findAllByCount(){ return kpcfMapper.findAllByCount();}

}

