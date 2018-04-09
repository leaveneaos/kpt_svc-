package com.rjxx.taxeasy.dao.orm;

import com.rjxx.comm.mybatis.MybatisRepository;
import com.rjxx.comm.mybatis.Pagination;
import com.rjxx.taxeasy.dao.bo.Xf;
import com.rjxx.taxeasy.dao.vo.XfKzVo;
import com.rjxx.taxeasy.dao.vo.XfVo;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/9.
 */
@MybatisRepository
public interface XfMapper {
	
	public Xf findOneByParams(Xf xf);
	
	public List<Xf> findAllByParams(Xf xf);
	
	public List<Xf> findAllByMap(Map<String, Object> map);

    public List<Xf> findByPage(Pagination pagination);

    public List<XfVo> findByPages(Pagination pagination);


    /**
     * 根据yhid查找该用户下的所有管理的销方
     * @param yhId
     * @return
     */
    public List<Xf> getXfListByYhId(int yhId);

	public Object findByPages(Map map);

	public XfVo findAllByXfxx(Map map);

	public List<XfKzVo> findXfkzListByXfid(Map map);
}
