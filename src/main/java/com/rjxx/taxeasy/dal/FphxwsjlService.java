package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Fphxwsjl;
import com.rjxx.taxeasy.dao.orm.FphxwsjlJpaDao;
import com.rjxx.taxeasy.dao.orm.FphxwsjlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Jun 13 13:21:38 CST 2017
 *
 * @ZhangBing
 */ 
@Service
public class FphxwsjlService {

    @Autowired
    private FphxwsjlJpaDao fphxwsjlJpaDao;

    @Autowired
    private FphxwsjlMapper fphxwsjlMapper;

    public Fphxwsjl findOne(int id) {
        return fphxwsjlJpaDao.findOne(id);
    }

    public void save(Fphxwsjl fphxwsjl) {
        fphxwsjlJpaDao.save(fphxwsjl);
    }

    public void save(List<Fphxwsjl> fphxwsjlList) {
        fphxwsjlJpaDao.save(fphxwsjlList);
    }

    public Fphxwsjl findOneByParams(Map params) {
        return fphxwsjlMapper.findOneByParams(params);
    }

    public List<Fphxwsjl> findAllByParams(Map params) {
        return fphxwsjlMapper.findAllByParams(params);
    }

    public List<Fphxwsjl> findByPage(Pagination pagination) {
        return fphxwsjlMapper.findByPage(pagination);
    }

}

