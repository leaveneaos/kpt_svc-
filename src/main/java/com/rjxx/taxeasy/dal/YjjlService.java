package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Yjjl;
import com.rjxx.taxeasy.dao.orm.YjjlJpaDao;
import com.rjxx.taxeasy.dao.orm.YjjlMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Dec 20 12:04:34 CST 2016
 *
 * @author ZhangBing
 */ 
@Service
public class YjjlService {

    @Autowired
    private YjjlJpaDao yjjlJpaDao;

    @Autowired
    private YjjlMapper yjjlMapper;

    public Yjjl findOne(int id) {
        return yjjlJpaDao.findOne(id);
    }

    public void save(Yjjl yjjl) {
        yjjlJpaDao.save(yjjl);
    }

    public void save(List<Yjjl> yjjlList) {
        yjjlJpaDao.save(yjjlList);
    }

    public Yjjl findOneByParams(Map params) {
        return yjjlMapper.findOneByParams(params);
    }

    public List<Yjjl> findAllByParams(Map params) {
        return yjjlMapper.findAllByParams(params);
    }

    public List<Yjjl> findByPage(Pagination pagination) {
        return yjjlMapper.findByPage(pagination);
    }

}

