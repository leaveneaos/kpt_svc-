package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Kpcf;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Aug 07 20:20:43 CST 2018
 *
 * @administrator
 */ 
@MybatisRepository
public interface KpcfMapper {

    public Kpcf findOneByParams(Map params);

    public List<Kpcf> findAllByParams(Map params);

    public List<Kpcf> findByPage(Pagination pagination);

    public void deleteById(int id);

    public List<Kpcf> findAllByCount();



}

