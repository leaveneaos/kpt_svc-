package com.rjxx.taxeasy.service;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.YhJpaDao;
import com.rjxx.taxeasy.dao.YhMapper;
import com.rjxx.taxeasy.domains.Yh;
import com.rjxx.taxeasy.vo.YhVO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Dec 01 12:47:48 CST 2016
 *
 * @ZhangBing
 */ 
@Service
public class YhService {

    @Autowired
    private YhJpaDao yhJpaDao;

    @Autowired
    private YhMapper yhMapper;

    public Yh findOne(int id) {
        return yhJpaDao.findOne(id);
    }

    public void save(Yh yh) {
        yhJpaDao.save(yh);
    }

    public void save(List<Yh> yhList) {
        yhJpaDao.save(yhList);
    }

    public Yh findOneByParams(Map params) {
        return yhMapper.findOneByParams(params);
    }

    public Yh findOneByParam(Map params) {
        return yhMapper.findOneByParam(params);
    }

    public List<Yh> findAllByParams(Map params) {
        return yhMapper.findAllByParams(params);
    }

    public List<Yh> findByPage(Pagination pagination) {
        return yhMapper.findByPage(pagination);
    }

	public YhVO findOneByYhVo(Map map) {
		// TODO Auto-generated method stub
		  return yhMapper.findOneByYhVo(map);
	}

	public YhVO findAllByYHCount(Map map) {
		// TODO Auto-generated method stub
		return yhMapper.findAllByYHCount(map);
	}

}

