package com.wuxianedu.domain.page;

import java.util.List;
public class ResultBean<T> {
	private List<T> beanList;
	
	private int totalRecord;

	
	public List<T> getBeanList() {
		return beanList;
	}


	public void setBeanList(List<T> beanList) {
		this.beanList = beanList;
	}


	public ResultBean() {
		super();
	}


	public int getTotalRecord() {
		return totalRecord;
	}

	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	
	
}
