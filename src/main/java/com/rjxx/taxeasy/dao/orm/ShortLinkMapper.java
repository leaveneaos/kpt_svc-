package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.ShortLink;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Jun 26 18:11:06 CST 2018
 *
 * @administrator
 */ 
@MybatisRepository
public interface ShortLinkMapper {

    public ShortLink findOneByParams(Map params);

    public List<ShortLink> findAllByParams(Map params);

    public List<ShortLink> findByPage(Pagination pagination);

}

