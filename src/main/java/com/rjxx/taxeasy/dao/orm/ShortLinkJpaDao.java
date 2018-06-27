package com.rjxx.taxeasy.dao.orm;

import com.rjxx.taxeasy.dao.bo.ShortLink;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Jun 26 18:11:06 CST 2018
 *
 * @administrator
 */ 
public interface ShortLinkJpaDao extends CrudRepository<ShortLink, Integer> {
    @Query(nativeQuery = true, value = "select * from t_short_link where short_link=?1 and use_mark='1'")
    ShortLink findOneByShortLink(String short_link);
}

