package com.rjxx.taxeasy.dao.bo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * t_dyzl 实体类
 * 由GenEntityMysql类自动生成
 * Tue Jan 17 15:10:28 CST 2017
 * @ZhangBing
 */ 
@Entity
@Table(name="t_dyzl")
public class Dyzl  implements Serializable {

@Id
@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Integer id;

/**
 * 订阅标题
 */ 
@Column(name="dybt")
	protected String dybt;

@Column(name="bz")
	protected String bz;


	public Integer getId(){
		return id;
	}

	public void setId(Integer id){
		this.id=id;
	}

	public String getDybt(){
		return dybt;
	}

	public void setDybt(String dybt){
		this.dybt=dybt;
	}

	public String getBz(){
		return bz;
	}

	public void setBz(String bz){
		this.bz=bz;
	}

}

