package com.rjxx.taxeasy.dal;

import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Csb;
import com.rjxx.taxeasy.dao.bo.Cszb;
import com.rjxx.taxeasy.dao.orm.CszbJpaDao;
import com.rjxx.taxeasy.dao.orm.CszbMapper;
import com.rjxx.taxeasy.dao.vo.CsbVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 由GenJavaCode类自动生成
 * <p>
 * Wed Nov 02 13:27:26 CST 2016
 *
 * @author 许黎明
 */ 
@Service
public class CszbService {

    @Autowired
    private CszbJpaDao cszbJpaDao;

    @Autowired
    private CszbMapper cszbMapper;
    @Autowired
    private CsbService csbService;
    public Cszb findOne(int id) {
        return cszbJpaDao.findOne(id);
    }

    public void save(Cszb cszb) {
        cszbJpaDao.save(cszb);
    }

    public void save(List<Cszb> cszbList) {
        cszbJpaDao.save(cszbList);
    }

    public Cszb findOneByParams(Cszb params) {
        return cszbMapper.findOneByParams(params);
    }

    public List<Cszb> findAllByParams(Map params) {
        return cszbMapper.findAllByParams(params);
    }

    public List<CsbVo> findByPage(Pagination pagination) {
        return cszbMapper.findByPage(pagination);
    }

	/**
	 * 	其实是根据公司代码，销方id，和skp表id,取该csm的值
	 */
    public Cszb getSpbmbbh(String gsdm,Integer xfid,Integer kpdid,String csm){
    	Map params = new HashMap<>();
		params.put("gsdm", gsdm);
		params.put("csm", csm);
		List<Cszb> list = new ArrayList<>();
		list =  cszbMapper.findAllByParams(params);
		if (list.size() > 0) {
			for (Cszb cszb : list) {
				if (null != kpdid && kpdid.equals(cszb.getKpdid())) {
					return cszb;
				}
			}
			for (Cszb cszb : list) {
				if (null != xfid && xfid.equals(cszb.getXfid()) && null == cszb.getKpdid()) {
					return cszb;
				}
			}
			for (Cszb cszb : list) {
				if (null != gsdm && gsdm.equals(cszb.getGsdm()) && null == cszb.getKpdid() && null == cszb.getXfid()) {
					return cszb;
				}
			}
		}
		Csb csb = csbService.findOneByParams(params);
		Cszb cszb = new Cszb();
		cszb.setCsz(csb.getMrz());
		return cszb;
    }

	public Cszb findsfzlkpByParams(Cszb cszb) {
		// TODO Auto-generated method stub
		return cszbMapper.findsfzlkpByParams(cszb);
	}

}

