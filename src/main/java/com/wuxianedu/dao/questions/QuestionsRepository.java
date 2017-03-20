package com.wuxianedu.dao.questions;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wuxianedu.domain.page.QueryInfo;
import com.wuxianedu.domain.questions.Questions;

public interface QuestionsRepository extends JpaRepository<Questions, Integer> {
	/*把数据加载到csv但是字段排列有问题*/
	@Query(value = "Select * From questions Into OutFile 'c:/temp/thesecond.csv';", nativeQuery = true)
	void getQuestionThenToCsv();
	
	@Query(value = "load data infile 'c:/temp/thesecond.csv' ignore into table questions fields terminated by ',' enclosed by '\"' lines terminated by '\n' ignore 1 lines (optiona,optionb,optionc,optiond,question,tqtitle,type_tid,createdate);", nativeQuery = true)
	void CsvToMysql();
	/*吧csv文件加载进入数据库*/
	@Query(value = "load data infile ?1 ignore into table questions fields terminated by ',' enclosed by '\"' lines terminated by '\n' ignore 1 lines (optiona,optionb,optionc,optiond,question,tqtitle,type_tid,createdate);", nativeQuery = true)
	void CsvToMysql2(String filename);

	/*删除全部记录*/
	@Query(value = "truncate table questions", nativeQuery = true)
	void deleteAllQuestion();

	/**
	 * 通过主键查找
	 * 
	 * @param tqid
	 * @return
	 */
	Questions findByTqid(Integer tqid);

	/**
	 * 通过多个主键查找
	 * 
	 * @param tqid
	 * @return
	 */
	@Query(value = "FROM Questions q where q.tqid in (:#{#tqids}) order by q.createdate desc")
	List<Questions> findByTqids(@Param("tqids") List<String> tqids);

	/**
	 * 吧主键赋给thedesc这个排序字段
	 * 
	 * @param questions
	 */
	@Query("update Questions q set q.createdate=:#{#questions.tqid} where q.tqid=:#{#questions.tqid}")
	@Modifying
	@Transactional
	void updateByThedesc(@Param("questions") Questions questions);

	/**
	 * 修改试题，有问题没有使用
	 * 
	 * @param questions
	 */
	@Query("update Questions q set q.tqtitle=:#{#questions.tqtitle},q.optiona=:#{#questions.optiona},q.optionb=:#{#questions.optionb},q.createdate=:#{#questions.createdate},q.optiond=:#{#questions.optiond},q.question=:#{#questions.question},q.type=:#{#questions.type.tid} where q.tqid=:#{#questions.tqid}")
	@Modifying
	@Transactional
	void updateOneQuestion(@Param("questions") Questions questions);

	/**
	 * 获取分页的questions
	 * 
	 * @param queryInfo
	 * @return
	 */
	@Query(value = "select * from questions order by createdate desc limit :#{#queryInfo.startIndex},:#{#queryInfo.pageSize} ", nativeQuery = true)
	List<Questions> getQuestionsByqueryinfo(@Param("queryInfo") QueryInfo queryInfo);
	/**
	 * 获取导入csv文件需要的
	 * 
	 * @param queryInfo
	 * @return
	 */
	@Query(value = "select * from questions limit ?1,?2 ", nativeQuery = true)
	List<Questions> getQuestionsByLimitTqid(Integer start , Integer pagenum);

	/**
	 * 获取一共多少条数据
	 */
	@Query(value = "select count(*) from Questions", nativeQuery = true)
	Integer getQuestionsNumByqueryinfo();

	/**
	 * 获取标题类型分页的questions
	 * 
	 * @param queryInfo
	 * @return
	 */
	@Query(value = "select * from questions q,type t where q.tqtitle like CONCAT('%',?3,'%') and t.tname = ?4 and q.type_tid=t.tid order by createdate desc limit ?1,?2", nativeQuery = true)
	List<Questions> getQuestionsByqueryinfoAndTqtitle(Integer startIndex, Integer pageSize, String tqtitle,
			String findtype);

	/**
	 * 获取按照标题分页多少条数据
	 */
	@Query(value = "select count(*) from questions q,type t where q.tqtitle like CONCAT('%',?1,'%') and t.tname = ?2 and q.type_tid=t.tid order by createdate desc", nativeQuery = true)
	Integer getQuestionsNumByTqtitle(String tqtitle, String findtype);

	@Query(value = "select * from questions q,type t where q.tqtitle like CONCAT('%',?1,'%') and t.tname = ?2 and q.type_tid=t.tid", nativeQuery = true)
	List<Questions> MyfindByTnameAndTqtitleLike(String tqtitle, String tname);
}
