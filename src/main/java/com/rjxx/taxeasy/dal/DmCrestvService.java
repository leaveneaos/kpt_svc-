package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.orm.DmCrestvJpaDao;
import com.rjxx.taxeasy.dao.orm.DmCrestvMapper;
import com.rjxx.taxeasy.dao.bo.DmCrestv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Wed Jul 18 09:47:04 CST 2018
 *
 * @administrator
 */ 
@Service
public class DmCrestvService {

    @Autowired
    private DmCrestvJpaDao dmCrestvJpaDao;

    @Autowired
    private DmCrestvMapper dmCrestvMapper;

    public DmCrestv findOne(int id) {
        return dmCrestvJpaDao.findOne(id);
    }

    public void save(DmCrestv dmCrestv) {
        dmCrestvJpaDao.save(dmCrestv);
    }

    public void save(List<DmCrestv> dmCrestvList) {
        dmCrestvJpaDao.save(dmCrestvList);
    }

    public DmCrestv findOneByParams(Map params) {
        return dmCrestvMapper.findOneByParams(params);
    }

    public List<DmCrestv> findAllByParams(Map params) {
        return dmCrestvMapper.findAllByParams(params);
    }

    public List<DmCrestv> findByPage(Pagination pagination) {
        return dmCrestvMapper.findByPage(pagination);
    }

}

