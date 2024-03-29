package com.rjxx.taxeasy.dao.orm;

import com.rjxx.taxeasy.dao.bo.Pp;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Mon Dec 12 17:15:27 GMT+08:00 2016
 *
 * @ZhangBing
 */ 
public interface PpJpaDao extends CrudRepository<Pp, Integer> {
    @Query(nativeQuery = true, value = "select * from t_pp where id=?1 and yxbz='1'")
    Pp findOneById(Integer id);
    @Query(nativeQuery = true, value = "select * from t_pp where ppdm=?1 and yxbz='1' ")
    Pp findOneByPpdm(String ppdm);
}

