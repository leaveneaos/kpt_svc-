package com.rjxx.taxeasy.dao.bo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * t_xxts 实体类
 * 由GenEntityMysql类自动生成
 * Tue Dec 27 09:18:59 CST 2016
 * @ZhangBing
 */ 
@Entity
@Table(name="t_xxts")
public class Xxts  implements Serializable {

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;

@Column(name="xm")
	protected String xm;

@Column(name="yx")
	protected String yx;

@Column(name="yxbz")
	protected String yxbz;


	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id=id;
	}

	public String getXm(){
		return xm;
	}

	public void setXm(String xm){
		this.xm=xm;
	}

	public String getYx(){
		return yx;
	}

	public void setYx(String yx){
		this.yx=yx;
	}

	public String getYxbz(){
		return yxbz;
	}

	public void setYxbz(String yxbz){
		this.yxbz=yxbz;
	}

}

