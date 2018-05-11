package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.orm.SeqnumberJpaDao;
import com.rjxx.taxeasy.dao.orm.SeqnumberMapper;
import com.rjxx.taxeasy.dao.bo.Seqnumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Fri May 11 14:40:00 CST 2018
 *
 * @administrator
 */ 
@Service
public class SeqnumberService {

    @Autowired
    private SeqnumberJpaDao seqnumberJpaDao;

    @Autowired
    private SeqnumberMapper seqnumberMapper;

    public Seqnumber findOne(int id) {
        return seqnumberJpaDao.findOne(id);
    }

    public void save(Seqnumber seqnumber) {
        seqnumberJpaDao.save(seqnumber);
    }

    public void save(List<Seqnumber> seqnumberList) {
        seqnumberJpaDao.save(seqnumberList);
    }

    public Seqnumber findOneByParams(Map params) {
        return seqnumberMapper.findOneByParams(params);
    }

    public List<Seqnumber> findAllByParams(Map params) {
        return seqnumberMapper.findAllByParams(params);
    }

    public List<Seqnumber> findByPage(Pagination pagination) {
        return seqnumberMapper.findByPage(pagination);
    }

}

