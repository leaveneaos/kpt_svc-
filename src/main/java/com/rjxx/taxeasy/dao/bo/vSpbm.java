package com.rjxx.taxeasy.dao.bo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * v_spbm 实体类
 * VIEW
 * 由GenEntityMysql类自动生成
 * Mon Aug 21 18:39:46 CST 2017
 * @ZhangBing
 */ 
@Entity
@Table(name="v_spbm")
public class vSpbm  implements Serializable {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected String spbm;

@Column(name="yhzcbs")
	protected Long yhzcbs;

@Column(name="yhzcmc")
	protected String yhzcmc;

@Column(name="lslbz")
	protected String lslbz;


	public String getSpbm(){
		return spbm;
	}

	public void setSpbm(String spbm){
		this.spbm=spbm;
	}

	public Long getYhzcbs(){
		return yhzcbs;
	}

	public void setYhzcbs(Long yhzcbs){
		this.yhzcbs=yhzcbs;
	}

	public String getYhzcmc(){
		return yhzcmc;
	}

	public void setYhzcmc(String yhzcmc){
		this.yhzcmc=yhzcmc;
	}

	public String getLslbz(){
		return lslbz;
	}

	public void setLslbz(String lslbz){
		this.lslbz=lslbz;
	}

}

