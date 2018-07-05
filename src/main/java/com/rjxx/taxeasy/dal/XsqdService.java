package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.orm.XsqdJpaDao;
import com.rjxx.taxeasy.dao.orm.XsqdMapper;
import com.rjxx.taxeasy.dao.bo.Xsqd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Jul 05 09:30:30 CST 2018
 *
 * @administrator
 */ 
@Service
public class XsqdService {

    @Autowired
    private XsqdJpaDao xsqdJpaDao;

    @Autowired
    private XsqdMapper xsqdMapper;

    public Xsqd findOne(int id) {
        return xsqdJpaDao.findOne(id);
    }

    public void save(Xsqd xsqd) {
        xsqdJpaDao.save(xsqd);
    }

    public void save(List<Xsqd> xsqdList) {
        xsqdJpaDao.save(xsqdList);
    }

    public Xsqd findOneByParams(Map params) {
        return xsqdMapper.findOneByParams(params);
    }

    public List<Xsqd> findAllByParams(Map params) {
        return xsqdMapper.findAllByParams(params);
    }

    public List<Xsqd> findByPage(Pagination pagination) {
        return xsqdMapper.findByPage(pagination);
    }

}

