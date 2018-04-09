package com.rjxx.taxeasy.dao.orm;

import com.rjxx.taxeasy.dao.bo.WxToken;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by Administrator on 2017-09-18.
 */
public interface WxTokenJpaDao extends CrudRepository<WxToken,Integer> {

    @Query(nativeQuery = true,value = "select * from wx_token where flag=?1")
    WxToken findByFlag(String flag);
}
