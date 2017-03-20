package com.wuxianedu.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.jpa.repository.Query;

import com.wuxianedu.domain.page.PageBean;
import com.wuxianedu.domain.page.QueryInfo;
import com.wuxianedu.domain.questions.Questions;

public interface QuestionService {

	public void AddmanyQuestions(List<Questions> questionlist);
	
	public void AddmanyQuestions();

	public void AddoneQuestion(Questions questions);

	public void UpdateQuestion(Questions questions, String tqid);

	public Map getPaging(HttpServletRequest request, Map<String, Object> map);

	public List<Questions> findAll();

	public void save(Questions questions);

	public void updateByThedesc(Questions questions2);

	public Questions findByTqid(Integer tqid);
	
	public List<Questions> findByTqids(List<String> tqids);

	public void deleteAll(List<String> lists);

	public List<Questions> MyfindByTnameAndTqtitleLike(String findtitle, String findtype);

	/**
	 * 查询resultbean并且放入pagebean
	 */
	public PageBean getPageBean(QueryInfo queryInfo);

	/**
	 * 通过标题类型分页查询
	 * 
	 * @param queryInfo
	 * @param tqtitle
	 * @param findtype
	 * @return
	 */
	public PageBean getPageBeanByTqtitle(QueryInfo queryInfo, String tqtitle, String findtype);

	/**
	 * 获取分页的questions
	 * 
	 * @param queryInfo
	 * @return
	 */
	List<Questions> getQuestionsByLimitTqid(Integer start , Integer pagenum);

	/*获取一共第三条数据*/
	public Integer getQuestionsNumByqueryinfo();
}
