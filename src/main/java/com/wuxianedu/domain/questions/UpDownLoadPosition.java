package com.wuxianedu.domain.questions;

public class UpDownLoadPosition {
	// 上传到第几行了
	private Integer UpLoadDealrow = 0;
	// 有没有开始上传到服务器,s三种状态,begin还没有开始上传,Uploading正在上传,final上传结束
	private String BeginUpLoadOrOnt;
	// 一共要上传多少行
	private Integer UpLoadAllrow = 0;

	@Override
	public String toString() {
		return "UpDownLoadPosition [UpLoadDealrow=" + UpLoadDealrow + ", BeginUpLoadOrOnt=" + BeginUpLoadOrOnt
				+ ", UpLoadAllrow=" + UpLoadAllrow + "]";
	}

	public UpDownLoadPosition() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Integer getUpLoadDealrow() {
		return UpLoadDealrow;
	}

	public void setUpLoadDealrow(Integer upLoadDealrow) {
		UpLoadDealrow = upLoadDealrow;
	}

	public String getBeginUpLoadOrOnt() {
		return BeginUpLoadOrOnt;
	}

	public void setBeginUpLoadOrOnt(String beginUpLoadOrOnt) {
		BeginUpLoadOrOnt = beginUpLoadOrOnt;
	}

	public Integer getUpLoadAllrow() {
		return UpLoadAllrow;
	}

	public void setUpLoadAllrow(Integer upLoadAllrow) {
		UpLoadAllrow = upLoadAllrow;
	}

}
