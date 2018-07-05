package com.rjxx.taxeasy.dao.orm;

import com.rjxx.taxeasy.dao.bo.Xsqd;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Thu Jul 05 09:30:30 CST 2018
 *
 * @administrator
 */ 
public interface XsqdJpaDao extends CrudRepository<Xsqd, Integer> {

    @Query(nativeQuery = true, value = "select * from t_xsqd where orderTypeName=?1 and channel=?2 and platformName=?3 and use_mark='1' ")
    Xsqd findByOrderChannelPla(String orderTypeName,String channel ,String platformName);
}

