package com.rjxx.taxeasy.dao.bo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * t_bmbb 实体类
 * 由GenEntityMysql类自动生成
 * Mon Jan 16 14:03:26 GMT+08:00 2017
 * @ZhangBing
 */ 
@Entity
@Table(name="t_bmbb")
public class Bmbb  implements Serializable {

/**
 * 主键id
 */ 
@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;

/**
 * 编码版本号
 */ 
@Column(name="bbh")
	protected String bbh;

/**
 * 有效标志
 */ 
@Column(name="yxbz")
	protected String yxbz;


	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id=id;
	}

	public String getBbh(){
		return bbh;
	}

	public void setBbh(String bbh){
		this.bbh=bbh;
	}

	public String getYxbz(){
		return yxbz;
	}

	public void setYxbz(String yxbz){
		this.yxbz=yxbz;
	}

}

