package com.wuxianedu.domain.type;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity

public class Type {
	
	
	public Type() {
		super();
		// TODO Auto-generated constructor stub
	}
	@Id
	@GeneratedValue
	//typeID
	public Integer tid;
	
	//type name 
	public String tname;

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	@Override
	public String toString() {
		return "Type [tid=" + tid + ", tname=" + tname + "]";
	}
	
}
