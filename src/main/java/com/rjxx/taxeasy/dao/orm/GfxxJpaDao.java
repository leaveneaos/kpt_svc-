package com.rjxx.taxeasy.dao.orm;

import com.rjxx.taxeasy.dao.bo.Gfxx;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Feb 09 14:46:55 CST 2017
 *
 * @ZhangBing
 */ 
public interface GfxxJpaDao extends CrudRepository<Gfxx, Integer> {
    @Query(nativeQuery = true,value = "select * from t_gfxx  where yxbz='1' and  gfmc=?1 and gfsh=?2 limit 1")
    Gfxx  findOneByGfmcAndGfsh(String gfmc, String gfsh);
}

