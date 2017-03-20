package com.wuxianedu.domain.questions;

import java.util.Date;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.wuxianedu.domain.type.Type;

@Entity
public class Questions implements Cloneable {
	@Id
	@GeneratedValue
	private Integer tqid;
	private Date createdate;

	public Date getCreatedate() {
		return createdate;
	}

	public void setCreatedate(Date createdate) {
		this.createdate = createdate;
	}

	private String tqtitle;

	private String optiona;

	private String optionb;

	private String optionc;

	private String optiond;

	private String question;

	@ManyToOne
	@JoinColumn(name = "type_tid")
	private Type type;

	public Integer getTqid() {
		return tqid;
	}

	public void setTqid(Integer tqid) {
		this.tqid = tqid;
	}

	public String getTqtitle() {
		return tqtitle;
	}

	public void setTqtitle(String tqtitle) {
		this.tqtitle = tqtitle;
	}

	public String getOptiona() {
		return optiona;
	}

	public void setOptiona(String optiona) {
		this.optiona = optiona;
	}

	public String getOptionb() {
		return optionb;
	}

	public void setOptionb(String optionb) {
		this.optionb = optionb;
	}

	public String getOptionc() {
		return optionc;
	}

	public void setOptionc(String optionc) {
		this.optionc = optionc;
	}

	public String getOptiond() {
		return optiond;
	}

	public void setOptiond(String optiond) {
		this.optiond = optiond;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}


	public Questions() {
		super();
	}

	@Override
	public String toString() {
		return "Questions [tqid=" + tqid + ", createdate=" + createdate + ", tqtitle=" + tqtitle + ", optiona="
				+ optiona + ", optionb=" + optionb + ", optionc=" + optionc + ", optiond=" + optiond + ", question="
				+ question + ", type=" + type + "]";
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public int hashCode() {
		Random random = new Random();
		int num = random.nextInt(100000) + 1;
		return num;
	}
}
