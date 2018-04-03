package com.rjxx.taxeasy.dao;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.domains.IsvCorp;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Apr 13 17:34:00 CST 2017
 *
 * @ZhangBing
 */ 
@MybatisRepository
public interface IsvCorpMapper {

    public IsvCorp findOneByParams(Map params);

    public List<IsvCorp> findAllByParams(Map params);

    public List<IsvCorp> findByPage(Pagination pagination);

	public void deleteCorp(String receiveCorpId);

}

