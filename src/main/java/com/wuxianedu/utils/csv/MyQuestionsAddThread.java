/*package com.wuxianedu.utils.csv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.wuxianedu.domain.questions.Questions;
import com.wuxianedu.domain.type.Type;

public class MyQuestionsAddThread extends Thread {
	Questions questions2 =new Questions(); 
	@Override
	public void run() {
		 String url = "jdbc:mysql://localhost:3308/questiontest";  
		 String name = "com.mysql.jdbc.Driver";  
		 String user = "root";  
		 String password = "root";  
		Connection conn = null;  
		try {
			Class.forName(name);
			conn = DriverManager.getConnection(url, user, password);//获取连接  
			conn.setAutoCommit(false);//关闭自动提交，不然conn.commit()运行到这句会报错
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	// 开始时间
	Long begin = new Date().getTime();
	// sql前缀
	String prefix = "insert into questions(tqtitle,optiona,optionb,optionc,optiond,question,type_tid,thedesc) values ";
	try {
		// 保存sql后缀
		StringBuffer suffix = new StringBuffer();
		// 设置事务为非自动提交
		conn.setAutoCommit(false);
		// 比起st，pst会更好些
		PreparedStatement  pst = (PreparedStatement) conn.prepareStatement("");//准备执行语句
		// 外层循环，总提交事务次数
		for (int i = 1; i <= 10; i++) {
			suffix = new StringBuffer();
			// 第j次提交步长
			for (int j = 1; j <= 10000; j++) {
				// 构建SQL后缀
				Questions questions =getQuestion(j);
				suffix.append("('" +questions.getTqtitle()+"','"+questions.getOptiona()+"','"+questions.getOptionb()+"','"+questions.getOptionc()+"','"+questions.getOptiond()+"','"+questions.getQuestion()+"',"+questions.getType().getTid()+","+questions.getThedesc() +"),");
			}
			// 构建完整SQL
			String sql = prefix + suffix.substring(0, suffix.length() - 1);
			// 添加执行SQL
			pst.addBatch(sql);
			// 执行操作
			pst.executeBatch();
			// 提交事务
			conn.commit();
			// 清空上一次添加的数据
			suffix = new StringBuffer();
		}
		// 头等连接
		pst.close();
		conn.close();
	} catch (SQLException e) {
		e.printStackTrace();
	}
	// 结束时间
	Long end = new Date().getTime();
	// 耗时
	System.out.println("1万条数据插入花费时间 : " + (end - begin) / 1000 + " s"+"  插入完成");
}	
	
	Questions getQuestion(Integer i){
		
		questions2.setTqtitle("java试题");
		questions2.setOptiona("A.11");
		questions2.setOptionb("B.22");
		questions2.setOptionc("C.33");
		questions2.setOptiond("D.44");
		questions2.setQuestion("D");
		if (i%4==0) {
			Type type = new Type();
			type.setTid(1);
			questions2.setType(type);
		}else if (i%4==1) {
			Type type = new Type();
			type.setTid(2);
			questions2.setType(type);
		}else if (i%4==2) {
			Type type = new Type();
			type.setTid(3);
			questions2.setType(type);
		}else if (i%4==3) {
			Type type = new Type();
			type.setTid(4);
			questions2.setType(type);
		}
		return questions2;
	}
	
}
*/