package com.wuxianedu.domain.page;

import java.util.List;
public class PageBean {
	//当页 全部数据
	private List beanList;
	//总计多少条数据
	private int totalRecord;
	//每页数据量
	private int pageSize;
	//总共多少页
	private int totalPage;
	//当前页
	private int currentPage;
	//上一页
	private int previousPage;
	//下一页
	private int nextPage;
	//页码条
	private int[] pageBar;
	
	private String url;
	
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	public void setPreviousPage(int previousPage) {
		this.previousPage = previousPage;
	}
	public void setNextPage(int nextPage) {
		this.nextPage = nextPage;
	}
	public void setPageBar(int[] pageBar) {
		this.pageBar = pageBar;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List getBeanList() {
		return beanList;
	}
	public void setBeanList(List beanList) {
		this.beanList = beanList;
	}
	
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalPage() {
		if(this.totalRecord%this.pageSize==0){
			this.totalPage = this.totalRecord/this.pageSize;
		}else{
			this.totalPage = this.totalRecord/this.pageSize + 1;
		}
		return totalPage;
	}
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public int getPreviousPage() {
		
		this.previousPage = this.currentPage - 1;
		if(this.previousPage <=0){
			this.previousPage = 1;
		}
		return previousPage;
	}
	public int getNextPage() {
		this.totalPage=getTotalPage();
		this.nextPage = this.currentPage + 1;
		if(this.nextPage > this.totalPage){
			this.nextPage = this.totalPage;
		}
		return nextPage;
	}
	public int[] getPageBar() {
		this.totalPage=getTotalPage();
		int startIndex = this.currentPage - 5;
		int endIndex = this.currentPage + 4;
		
		if(this.totalPage<10){
			this.pageBar = new int[totalPage];
			startIndex = 1;
			endIndex = this.totalPage;
		}else{
			this.pageBar = new int[10];
			
			if(startIndex<=0){
				startIndex = 1;
				endIndex = 10;
			}
			if(endIndex>=this.totalPage){
				endIndex = this.totalPage;
				startIndex = this.totalPage - 9;
			}
		}
		int index = 0;
		for (int i = startIndex; i <= endIndex; i++) {
			this.pageBar[index++] =  i;
		}
	/*	this.pageBar = new int[10];
		for (int i = 0; i < this.pageBar.length; i++) {
			this.pageBar[i] = i + 1;
		}*/
		return pageBar;
	}

	
}