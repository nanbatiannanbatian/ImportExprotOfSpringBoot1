package com.wuxianedu.utils.batchaddandupdate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;

import com.wuxianedu.domain.questions.Questions;
@Controller
@EnableAutoConfiguration
public class BatchAdding {
	@Autowired
	JdbcTemplate jdbcTemplate;

	public void addBuyBean(List<Questions> list) {
		final List<Questions> tempBpplist = list;
		String sql = "insert into questions(createdate,optiona,optionb,optionc,optiond,question,tqtitle,type_tid)"
				+ " values(?,?,?,?,?,?,?,?)";
		if (jdbcTemplate==null) {
			System.out.println("-------------------it is null!!!!!-------------");
		}
		this.getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement arg0, int arg1) throws SQLException {
				arg0.setDate(1, new java.sql.Date(tempBpplist.get(arg1).getCreatedate().getTime()));
				arg0.setString(2, tempBpplist.get(arg1).getOptiona());
				arg0.setString(3, tempBpplist.get(arg1).getOptionb());
				arg0.setString(4, tempBpplist.get(arg1).getOptionc());
				arg0.setString(5, tempBpplist.get(arg1).getOptiond());
				arg0.setString(6, tempBpplist.get(arg1).getQuestion());
				arg0.setString(7, tempBpplist.get(arg1).getTqtitle());
				arg0.setInt(8, tempBpplist.get(arg1).getType().getTid());

			}

			@Override
			public int getBatchSize() {
				return tempBpplist.size();
			}
		});
	}
	
//    @Transactional("dbTransaction")  
    public void batchInsertJDBC2(List<Questions> poilist) throws DataAccessException {  
    	String sql = "insert into questions(createdate,optiona,optionb,optionc,optiond,question,tqtitle,type_tid)"
				+ " values(?,?,?,?,?,?,?,?)";
        List<Object[]> batchArgs = new ArrayList<Object[]>();  
        for (Questions poi : poilist) {  
            Object[] args = {new java.sql.Date(poi.getCreatedate().getTime()), poi.getOptiona(), poi.getOptionb(), poi.getOptionc(), poi.getOptiond(), poi.getQuestion(), poi.getTqtitle(), poi.getType().getTid()};  
            batchArgs.add(args);  
        }  
        jdbcTemplate.batchUpdate(sql, batchArgs);  
    }  

    /**
     * 批量更新从csv文件中读取的数据
     * 说明：会根据主键忽略重复数据
     * @param orderInfoList
     * 数据最好不要超过1万条左右
     */
    public void updateWinnerList(List<Questions> orderInfoList) {
        String prefix ="INSERT INTO questions(createdate,optiona,optionb,optionc,optiond,question,tqtitle,type_tid) VALUES \n";
        StringBuffer suffix = new StringBuffer();
        Questions questions;
        for (int i = 0,length = orderInfoList.size(); i < length; i++) {
        	questions = orderInfoList.get(i);
        	String createdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(questions.getCreatedate());
            suffix.append("('"+createdate+"','"+questions.getOptiona()+"','"+questions.getOptionb()+"','"+questions.getOptionc()+"','"+questions.getOptiond()+"','"+questions.getQuestion()+"','"+questions.getTqtitle()+"',"+questions.getType().getTid()+")\n,");
        }
        String sql = prefix+suffix.substring(0, suffix.length() - 2);
        jdbcTemplate.update(sql);
    }


    
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
}
