package com.rjxx.taxeasy.dao.bo;

import javax.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.rjxx.comm.json.JsonDateFormat;
import com.rjxx.comm.json.JsonDatetimeFormat;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
/**
 * t_dm_crestv 实体类
 * 凯盈盒子返回的代码表
 * 由GenEntityMysql类自动生成
 * Wed Jul 18 09:46:19 CST 2018
 * @administrator
 */ 
@Entity
@Table(name="t_dm_crestv")
public class DmCrestv  implements Serializable {

/**
 * 凯盈返回代码
 */
@Id
@Column(name="code")
	protected String code;

/**
 * 凯盈返回信息
 */ 
@Column(name="msg")
	protected String msg;

/**
 * 是否重复发送请求（1，是；0，否）
 */ 
@Column(name="repeat_send")
	protected String repeatSend;

/**
 * 是否生成新的申请号
 */ 
@Column(name="new_seqnumber")
	protected String newSeqnumber;


	public String getCode(){
		return code;
	}

	public void setCode(String code){
		this.code=code;
	}

	public String getMsg(){
		return msg;
	}

	public void setMsg(String msg){
		this.msg=msg;
	}

	public String getRepeatSend(){
		return repeatSend;
	}

	public void setRepeatSend(String repeatSend){
		this.repeatSend=repeatSend;
	}

	public String getNewSeqnumber(){
		return newSeqnumber;
	}

	public void setNewSeqnumber(String newSeqnumber){
		this.newSeqnumber=newSeqnumber;
	}

}

