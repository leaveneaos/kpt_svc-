package com.rjxx.taxeasy.dao.bo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * t_yjmb 实体类
 * 由GenEntityMysql类自动生成
 * Thu May 25 15:27:45 CST 2017
 * @ZhangBing
 */ 
@Entity
@Table(name="t_yjmb")
public class Yjmb  implements Serializable {

/**
 * 邮件模板代码
 */
@Id
@Column(name="yjmb_dm")
	protected Integer yjmbDm;

/**
 * 邮件模板名称
 */ 
@Column(name="yjmb_mc")
	protected String yjmbMc;

/**
 * 邮件模板内容
 */ 
@Column(name="yjmb_nr")
	protected String yjmbNr;

/**
 * 有效标志（1：有效；0：无效）
 */ 
@Column(name="yxbz")
	protected Integer yxbz;


	public Integer getYjmbDm(){
		return yjmbDm;
	}

	public void setYjmbDm(Integer yjmbDm){
		this.yjmbDm=yjmbDm;
	}

	public String getYjmbMc(){
		return yjmbMc;
	}

	public void setYjmbMc(String yjmbMc){
		this.yjmbMc=yjmbMc;
	}

	public String getYjmbNr(){
		return yjmbNr;
	}

	public void setYjmbNr(String yjmbNr){
		this.yjmbNr=yjmbNr;
	}

	public Integer getYxbz(){
		return yxbz;
	}

	public void setYxbz(Integer yxbz){
		this.yxbz=yxbz;
	}

}

