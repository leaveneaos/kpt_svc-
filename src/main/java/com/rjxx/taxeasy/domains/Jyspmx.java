package com.rjxx.taxeasy.domains;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.rjxx.comm.json.JsonDatetimeFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * t_jyspmx 实体类
 * 交易商品明细表 待开票明细和已开票金额
 * 由GenEntityMysql类自动生成
 * Mon Oct 10 16:06:40 CST 2016
 *
 * @ZhangBing
 */
@Entity
@Table(name = "t_jyspmx")
public class Jyspmx implements Serializable {
	
	

    public Jyspmx() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Jyspmx(Integer spmxxh, String fphxz, String spdm, String spmc, String spggxh, String spdw, Double sps,
			Double spdj, Double spje, Double spsl, Double spse, Double jshj, Double ykphj, Integer hzkpxh, Date lrsj,
			Integer lrry, Date xgsj, Integer xgry, String gsdm, Integer xfid, Integer skpid,Double kce,String yhzcbs,String yhzcmc,String lslbz) {
		super();
		this.spmxxh = spmxxh;
		this.fphxz = fphxz;
		this.spdm = spdm;
		this.spmc = spmc;
		this.spggxh = spggxh;
		this.spdw = spdw;
		this.sps = sps;
		this.spdj = spdj;
		this.spje = spje;
		this.spsl = spsl;
		this.spse = spse;
		this.jshj = jshj;
		this.ykphj = ykphj;
		this.hzkpxh = hzkpxh;
		this.lrsj = lrsj;
		this.lrry = lrry;
		this.xgsj = xgsj;
		this.xgry = xgry;
		this.gsdm = gsdm;
		this.xfid = xfid;
		this.skpid = skpid;
		this.kce = kce;
		this.yhzcbs = yhzcbs;
		this.yhzcmc = yhzcmc;
		this.lslbz = lslbz;
	}

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Integer id;

    /**
     * 单据号
     */
    @Column(name = "djh")
    protected Integer djh;

    /**
     * 商品明细序号
     */
    @Column(name = "spmxxh")
    protected Integer spmxxh;

    /**
     * 0、正常行；1、折扣行；2、被折扣行
     */
    @Column(name = "fphxz")
    protected String fphxz;

    /**
     * 商品代码
     */
    @Column(name = "spdm")
    protected String spdm;

    /**
     * 商品名称
     */
    @Column(name = "spmc")
    protected String spmc;

    /**
     * 商品规格型号
     */
    @Column(name = "spggxh")
    protected String spggxh;

    /**
     * 商品单位
     */
    @Column(name = "spdw")
    protected String spdw;

    /**
     * 商品数量
     */
    @Column(name = "sps")
    protected Double sps;

    /**
     * 商品单价
     */
    @Column(name = "spdj")
    protected Double spdj;

    /**
     * 商品金额
     */
    @Column(name = "spje")
    protected Double spje;

    /**
     * 商品税率
     */
    @Column(name = "spsl")
    protected Double spsl;

    /**
     * 商品税额
     */
    @Column(name = "spse")
    protected Double spse;
    

    /**
     * 价税合计
     */
    @Column(name = "jshj")
    protected Double jshj;

    /**
     * 已开票价税合计 已开票价税合计
     */
    @Column(name = "ykphj")
    protected Double ykphj;

    /**
     * 汇总开票序号
     */
    @Column(name = "hzkpxh")
    protected Integer hzkpxh;

    /**
     * 录入时间
     */
    @Column(name = "lrsj")
    @JsonSerialize(using = JsonDatetimeFormat.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date lrsj;

    /**
     * 录入人员
     */
    @Column(name = "lrry")
    protected Integer lrry;

    /**
     * 修改时间
     */
    @Column(name = "xgsj")
    @JsonSerialize(using = JsonDatetimeFormat.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected Date xgsj;

    /**
     * 修改人员
     */
    @Column(name = "xgry")
    protected Integer xgry;

    /**
     * 公司代码
     */
    @Column(name = "gsdm")
    protected String gsdm;
	/**
	 * 税控盘id
	 */
	@Column(name = "skpid")
	protected Integer skpid;

    /**
     * 销方id
     */
    @Column(name = "xfid")
    protected Integer xfid;

    /**
     * 扣除额
     */
    @Column(name = "kce")
    protected Double kce;
    
    /**
     * 优惠政策标识
     */
    @Column(name = "yhzcbs")
    protected String yhzcbs;
    
    /**
     * 优惠政策名称
     */
    @Column(name = "yhzcmc")
    protected String yhzcmc;
    
    /**
     * 零税率标志
     */
    @Column(name = "lslbz")
    protected String lslbz;
    
    public Double getKce() {
		return kce;
	}

	public void setKce(Double kce) {
		this.kce = kce;
	}

	public String getYhzcbs() {
		return yhzcbs;
	}

	public void setYhzcbs(String yhzcbs) {
		this.yhzcbs = yhzcbs;
	}

	public String getYhzcmc() {
		return yhzcmc;
	}

	public void setYhzcmc(String yhzcmc) {
		this.yhzcmc = yhzcmc;
	}

	public String getLslbz() {
		return lslbz;
	}

	public void setLslbz(String lslbz) {
		this.lslbz = lslbz;
	}

	public Integer getXfid() {
		return xfid;
	}

	public void setXfid(Integer xfid) {
		this.xfid = xfid;
	}

	public Integer getSkpid() {
		return skpid;
	}

	public void setSkpid(Integer skpid) {
		this.skpid = skpid;
	}

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDjh() {
        return djh;
    }

    public void setDjh(Integer djh) {
        this.djh = djh;
    }

    public Integer getSpmxxh() {
        return spmxxh;
    }

    public void setSpmxxh(Integer spmxxh) {
        this.spmxxh = spmxxh;
    }

    public String getFphxz() {
        return fphxz;
    }

    public void setFphxz(String fphxz) {
        this.fphxz = fphxz;
    }

    public String getSpdm() {
        return spdm;
    }

    public void setSpdm(String spdm) {
        this.spdm = spdm;
    }

    public String getSpmc() {
        return spmc;
    }

    public void setSpmc(String spmc) {
        this.spmc = spmc;
    }

    public String getSpggxh() {
        return spggxh;
    }

    public void setSpggxh(String spggxh) {
        this.spggxh = spggxh;
    }

    public String getSpdw() {
        return spdw;
    }

    public void setSpdw(String spdw) {
        this.spdw = spdw;
    }

    public Double getSps() {
        return sps;
    }

    public void setSps(Double sps) {
        this.sps = sps;
    }

    public Double getSpdj() {
        return spdj;
    }

    public void setSpdj(Double spdj) {
        this.spdj = spdj;
    }

    public Double getSpje() {
        return spje;
    }

    public void setSpje(Double spje) {
        this.spje = spje;
    }

    public Double getSpsl() {
        return spsl;
    }

    public void setSpsl(Double spsl) {
        this.spsl = spsl;
    }

    public Double getSpse() {
        return spse;
    }

    public void setSpse(Double spse) {
        this.spse = spse;
    }

    public Double getJshj() {
        return jshj;
    }

    public void setJshj(Double jshj) {
        this.jshj = jshj;
    }

    public Double getYkphj() {
        return ykphj;
    }

    public void setYkphj(Double ykphj) {
        this.ykphj = ykphj;
    }

    public Integer getHzkpxh() {
        return hzkpxh;
    }

    public void setHzkpxh(Integer hzkpxh) {
        this.hzkpxh = hzkpxh;
    }

    public Date getLrsj() {
        return lrsj;
    }

    public void setLrsj(Date lrsj) {
        this.lrsj = lrsj;
    }

    public Integer getLrry() {
        return lrry;
    }

    public void setLrry(Integer lrry) {
        this.lrry = lrry;
    }

    public Date getXgsj() {
        return xgsj;
    }

    public void setXgsj(Date xgsj) {
        this.xgsj = xgsj;
    }

    public Integer getXgry() {
        return xgry;
    }

    public void setXgry(Integer xgry) {
        this.xgry = xgry;
    }

    public String getGsdm() {
        return gsdm;
    }

    public void setGsdm(String gsdm) {
        this.gsdm = gsdm;
    }

}

