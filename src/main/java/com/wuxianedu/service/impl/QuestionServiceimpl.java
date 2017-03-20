package com.wuxianedu.service.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.codehaus.groovy.util.Finalizable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.wuxianedu.dao.questions.QuestionsRepository;
import com.wuxianedu.dao.type.TypeRepository;
import com.wuxianedu.domain.page.PageBean;
import com.wuxianedu.domain.page.QueryInfo;
import com.wuxianedu.domain.questions.Questions;
import com.wuxianedu.domain.type.Type;
import com.wuxianedu.service.QuestionService;

@Service
public class QuestionServiceimpl implements QuestionService {
	@Autowired
	QuestionsRepository questionsRepository;
	@Autowired
	TypeRepository typeRepository;
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	/**
	 * 添加多条记录到数据库，但是添加的每道试题数据要有tqid  thedesc添加的默认值为1
	 * @param questionlist
	 * @return
	 */
	@Override
	public void AddmanyQuestions(final List<Questions> questionlist) {
		  final List<Questions> tempqueslist = questionlist;   
	       String sql="insert into questions(tqtitle,optiona,optionb,optionc,optiond,question,type_tid,thedesc)" +  
	            " values(?,?,?,?,?,?,?,?)"; 
	       
	       jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
			
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, tempqueslist.get(i).getTqtitle());
				ps.setString(2, tempqueslist.get(i).getOptiona());
				ps.setString(3, tempqueslist.get(i).getOptionb());
				ps.setString(4, tempqueslist.get(i).getOptionc());
				ps.setString(5, tempqueslist.get(i).getOptiond());
				ps.setString(6, tempqueslist.get(i).getQuestion());
				ps.setInt(7, tempqueslist.get(i).getType().getTid());
				ps.setInt(8, 1);
				System.out.println("i:"+i);
			}
			
			@Override
			public int getBatchSize() {
				return questionlist.size();
			}
		});
	       
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	@Override
	public void AddoneQuestion(Questions questions) {
		try {
			Type type = typeRepository.findByTname(questions.getType().getTname());
			questions.setType(type);
			
			SimpleDateFormat bartDateFormat =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date newdate=new Date();
			String createdate = bartDateFormat.format(newdate);
			Date dateinput = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(createdate);
			questions.setCreatedate(dateinput);
			
			questionsRepository.save(questions);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void UpdateQuestion(Questions questions, String tqid) {
		Questions questions2;
		Questions questions3;
		try {
			if ((questions2 = questionsRepository.findByTqid(Integer.valueOf(tqid))) == null) {
			} else {
			}

			questionsRepository.delete(Integer.valueOf(tqid));
			Type type = typeRepository.findByTname(questions.getType().getTname());
			questions.setType(type);
			questions.setCreatedate(questions2.getCreatedate());
			// 直接使用questions保存的话主键会变成第一次保存的，以后每次在保存会直接覆盖上一次的，所以使用克隆
			questions3 = (Questions) questions.clone();
			questionsRepository.save(questions3);
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map getPaging(HttpServletRequest request, Map<String, Object> map) {
		HttpSession session = request.getSession();
		if (session.getAttribute("alltqtitle") == null) {
			map.put("alltqtitle", "");
		} else {
			map.put("alltqtitle", session.getAttribute("alltqtitle"));
		}
		if (session.getAttribute("tname") == null) {
			map.put("tname", "");
		} else {
			map.put("tname", session.getAttribute("tname"));
		}
		if (session.getAttribute("url") == null) {
			map.put("url", "/question/findAllByPage");
		} else {
			map.put("url", session.getAttribute("url"));
		}
		if (session.getAttribute("currentPage") == null || session.getAttribute("currentPage").equals("")) {
			map.put("currentPage", "1");
		} else {
			map.put("currentPage", session.getAttribute("currentPage"));
		}
		return map;
	}

	@Override
	public List<Questions> findAll() {
		return questionsRepository.findAll();
	}

	@Override
	public void save(Questions questions) {
		questionsRepository.save(questions);
	}

	@Override
	public void updateByThedesc(Questions questions2) {
		questionsRepository.updateByThedesc(questions2);
	}

	@Override
	public Questions findByTqid(Integer tqid) {
		return questionsRepository.findByTqid(tqid);
	}

	@Override
	public void deleteAll(List<String> lists) {
		for (String string : lists) {
			if (questionsRepository.findByTqid(Integer.valueOf(string)) == null) {
			} else {
				questionsRepository.delete(Integer.valueOf(string));
			}
		}
	}

	@Override
	public List<Questions> MyfindByTnameAndTqtitleLike(String findtitle, String findtype) {
		return questionsRepository.MyfindByTnameAndTqtitleLike(findtitle, findtype);
	}

	/**
	 * 查询resultbean并且放入pagebean
	 */
	@Override
	public PageBean getPageBean(QueryInfo queryInfo) {
		PageBean pageBean = new PageBean();
		pageBean.setCurrentPage(queryInfo.getCurrentPage());
		pageBean.setPageSize(queryInfo.getPageSize());
		pageBean.setTotalRecord(questionsRepository.getQuestionsNumByqueryinfo());
		pageBean.setBeanList(questionsRepository.getQuestionsByqueryinfo(queryInfo));

		/*
		 * pageBean.setPageBar(pageBean.getPageBar());
		 * pageBean.setTotalPage(pageBean.getTotalPage());
		 */

		return pageBean;
	}

	/**
	 * 通过标题类型分页查询
	 * 
	 * @param queryInfo
	 * @param tqtitle
	 * @param findtype
	 * @return
	 */
	@Override
	public PageBean getPageBeanByTqtitle(QueryInfo queryInfo, String tqtitle, String findtype) {
		PageBean pageBean = new PageBean();
		pageBean.setCurrentPage(queryInfo.getCurrentPage());
		pageBean.setPageSize(queryInfo.getPageSize());
		pageBean.setTotalRecord(questionsRepository.getQuestionsNumByTqtitle(tqtitle, findtype));
		pageBean.setBeanList(questionsRepository.getQuestionsByqueryinfoAndTqtitle(queryInfo.getStartIndex(),
				queryInfo.getPageSize(), tqtitle, findtype));

		/*
		 * pageBean.setPageBar(pageBean.getPageBar());
		 * pageBean.setTotalPage(pageBean.getTotalPage());
		 */

		return pageBean;
	}

	@Override
	public void AddmanyQuestions() {
		
	}

	@Override
	public List<Questions> findByTqids(List<String> tqids) {
		return questionsRepository.findByTqids(tqids);
	}

	@Override
	public List<Questions> getQuestionsByLimitTqid(Integer start, Integer pagenum) {
		return questionsRepository.getQuestionsByLimitTqid(start, pagenum);
	}

	@Override
	public Integer getQuestionsNumByqueryinfo() {
		// TODO Auto-generated method stub
		return questionsRepository.getQuestionsNumByqueryinfo();
	}

}
