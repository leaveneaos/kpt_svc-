package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.orm.ShortLinkJpaDao;
import com.rjxx.taxeasy.dao.orm.ShortLinkMapper;
import com.rjxx.taxeasy.dao.bo.ShortLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Tue Jun 26 18:11:06 CST 2018
 *
 * @administrator
 */ 
@Service
public class ShortLinkService {

    @Autowired
    private ShortLinkJpaDao shortLinkJpaDao;

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    public ShortLink findOne(int id) {
        return shortLinkJpaDao.findOne(id);
    }

    public void save(ShortLink shortLink) {
        shortLinkJpaDao.save(shortLink);
    }

    public void save(List<ShortLink> shortLinkList) {
        shortLinkJpaDao.save(shortLinkList);
    }

    public ShortLink findOneByParams(Map params) {
        return shortLinkMapper.findOneByParams(params);
    }

    public List<ShortLink> findAllByParams(Map params) {
        return shortLinkMapper.findAllByParams(params);
    }

    public List<ShortLink> findByPage(Pagination pagination) {
        return shortLinkMapper.findByPage(pagination);
    }

}

