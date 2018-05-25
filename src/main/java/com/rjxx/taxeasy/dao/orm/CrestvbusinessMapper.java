package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Crestvbusiness;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Fri May 25 14:58:46 CST 2018
 *
 * @administrator
 */ 
@MybatisRepository
public interface CrestvbusinessMapper {

    public Crestvbusiness findOneByParams(Map params);

    public List<Crestvbusiness> findAllByParams(Map params);

    public List<Crestvbusiness> findByPage(Pagination pagination);

}

