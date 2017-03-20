package com.wuxianedu.domain.page;


public class QueryInfo {
	private Integer pageSize = 18;//每页多少条数据
	/**
	 * 当前第几页
	 */
	private Integer currentPage = 1;
	/**
	 * 第几条记录开始
	 */
	private Integer startIndex;
	
	public Integer getPageSize() {
		return pageSize;
	}
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}
	public Integer getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public Integer getStartIndex() {
		this.startIndex = (this.currentPage - 1)*this.pageSize;
		return startIndex;
	}
	public QueryInfo() {
		super();
	}
	@Override
	public String toString() {
		return "QueryInfo [pageSize=" + pageSize + ", currentPage="
				+ currentPage + ", startIndex=" + startIndex + "]";
	}
	
}
