package com.wuxianedu.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wuxianedu.domain.page.PageBean;
import com.wuxianedu.domain.page.QueryInfo;
import com.wuxianedu.domain.questions.Questions;
import com.wuxianedu.domain.type.Type;
import com.wuxianedu.service.QuestionService;
import com.wuxianedu.service.TypeService;

@Controller
@RequestMapping("/question")
@EnableAutoConfiguration
public class QuestionsController {
	@Autowired
	QuestionService questionService;
	@Autowired
	TypeService typeService;

	@RequestMapping("/addquestions")
	public void addUser() {
		/*questionService.AddmanyQuestions();*/
	}

	/**
	 * 查找所有类型在网页显现出来
	 * 
	 * @return
	 */
	@RequestMapping("/getQuestionHtml")
	public String getQuestionHtml() {
		return "jsps/questions/question";
	}

	/**
	 * 显示修改页面
	 * 
	 * @return
	 */
	@RequestMapping("/showupdateHtml")
	public String showupdateHtml() {
		return "jsps/questions/updateone";
	}

	/**
	 * 显示添加页面
	 * 
	 * @return
	 */
	@RequestMapping("/showaddHtml")
	public String showaddHtml() {
		return "jsps/questions/addone";
	}

	/**
	 * 显示信息页面
	 * 
	 * @return
	 */
	@RequestMapping("/showmessageHtml")
	public String showmessageHtml() {
		return "jsps/message";
	}
	/**
	 * 显示信息页面
	 * 
	 * @return
	 */
	@RequestMapping("/showimexprotHtml")
	public String showimexprotHtml() {
		return "jsps/questions/imexportquestions";
	}


	/**
	 * 查找所有类型在网页显现出来
	 * 
	 * @return
	 */
	@RequestMapping("/findallType")
	@ResponseBody
	public String FindallType() {
		List<Type> types = typeService.findall();
		String json = JSON.toJSONString(types, SerializerFeature.DisableCircularReferenceDetect);
		return json;
	}

	/**
	 * 通过分页记录
	 * 
	 * @return
	 */
	@RequestMapping("/findAllByPage")
	@ResponseBody
	public String findAllByPage(QueryInfo queryInfo, HttpServletRequest request) {
		PageBean pageBean = questionService.getPageBean(queryInfo);
		pageBean.setUrl(request.getRequestURI());
		// 避免前段处理相同对象的“循环引用检测”特性。
		String json = JSON.toJSONString(pageBean, SerializerFeature.DisableCircularReferenceDetect);
		// String json=JSON.toJSONString(questions);
		return json;
	}

	/**
	 * 通过试题标题查询分页
	 * 
	 * @return
	 */
	@RequestMapping("/findTqtitleAndTypeByPage")
	@ResponseBody
	public String FindTqtitleByPage(QueryInfo queryInfo, HttpServletRequest request, String findtitle,
			String findtype) {
		// 判断tqid是否或者数据库是否存在
		if (findtype == null && findtitle == null) {
			System.out.println("请输入数据");
			Addmessage(request, "请输入数据");
			return "请输入数据";
		}
		PageBean pageBean = questionService.getPageBeanByTqtitle(queryInfo, findtitle, findtype);
		setPageBean(request, pageBean);
		// 避免前段处理相同对象的“循环引用检测”特性。
		String json = JSON.toJSONString(pageBean, SerializerFeature.DisableCircularReferenceDetect);
		System.out.println(json);
		Addmessage(request, "查询成功");
		return json;
	}

	/**
	 * 添加一条记录 save操作以后要执行updateByThedesc把主键更新到thedesc这个字段
	 * 
	 * @param questions
	 * @return
	 */
	@RequestMapping("/addone")
	@ResponseBody
	public String Addone(Questions questions, HttpServletRequest request) {
		try {
			// 判断是否有输入想没有输入，是的话拒接该操作
			if (!QuestionskHasNullOrNot(questions)) {
				System.out.println("有的选项为空，请输入完全！");
				Addmessage(request, "有的选项为空，请输入完全！");
				return "有的选项为空，请输入完全！";
			} else if (!QuestionsIsToLongOrNot(questions)) {
				Addmessage(request, "输入的字符长度超过标准！请删减后再输入");
				return "输入的字符长度超过标准！请删减后再输入";
			}

			questionService.AddoneQuestion(questions);
			Addmessage(request, "添加成功");
			return "添加成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙请稍后操作，谢谢合作";
		}

	}

	@RequestMapping("/deleteone")
	@ResponseBody
	public String Deleteone(String totalId, HttpServletRequest request) {
		List<String> lists = Arrays.asList(totalId.split("_"));
		try {
			// 判断tqid是否或者数据库是否存在
			if (lists == null) {
				System.out.println("传过来的数据为空");
				Addmessage(request, "传过来的数据为空");
				return "传过来的数据为空";
			}
			questionService.deleteAll(lists);
			Addmessage(request, "删除成功");
			return "删除成功";

		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙请稍后操作，谢谢合作";
		}
	}

	@RequestMapping("/updateone")
	@ResponseBody
	public String Updateone(Questions questions, HttpServletRequest request) {
		String totalId = (String) request.getSession().getAttribute("totalId");
		List<String> lists = Arrays.asList(totalId.split("_"));
		try {
			// 判断tqid是否或者数据库是否存在
			if (lists == null) {
				System.out.println("没有输入id");
				Addmessage(request, "没有输入id");
				return "没有输入id";
			} else if (!QuestionsIsToLongOrNot(questions)) {
				Addmessage(request, "输入的字符长度超过标准！请删减后再输入");
				return "输入的字符长度超过标准！请删减后再输入";
			}
			// 判断输入是否完全
			if (!QuestionskHasNullOrNot(questions)) {
				System.out.println("不能有空项");
				Addmessage(request, "不能有空项,请输入完全");
				return "不能有空项,请输入完全";
			}
			for (String string : lists) {

				questionService.UpdateQuestion(questions, string);
			}
			Addmessage(request, "修改成功");
			return "修改成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙请稍后操作，谢谢合作";
		}
	}

	/**
	 * 保存所有totalId和第一个试题信息
	 * 
	 * @param totalId
	 * @param request
	 * @return
	 */
	@RequestMapping("/saveTotalIdAndQuestionAndCurrentPage")
	@ResponseBody
	public String SaveTotalIdAndQuestionAndCurrentPage(String totalId, HttpServletRequest request) {
		List<String> lists = Arrays.asList(totalId.split("_"));
		try {
			// 判断tqid是否或者数据库是否存在
			if (lists == null) {
				System.out.println("没有输入id");
				return "没有输入id";
			}
			String firstTqid = lists.get(0);
			Questions questions = questionService.findByTqid(Integer.valueOf(firstTqid));
			request.getSession().setAttribute("questions", questions);
			request.getSession().setAttribute("totalId", totalId);
			return "保存数据成功";
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙请稍后操作，谢谢合作";
		}
	}

	/**
	 * 获取所有totalId和第一个试题信息
	 * 
	 * @param totalId
	 * @param request
	 * @return
	 */
	@RequestMapping("/getQuestion")
	@ResponseBody
	public String getQuestion(HttpServletRequest request) {
		try {
			Questions questions = (Questions) request.getSession().getAttribute("questions");
			String json = JSON.toJSONString(questions, SerializerFeature.DisableCircularReferenceDetect);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙请稍后操作，谢谢合作";
		}
	}

	@RequestMapping("/findQuestionByTqid")
	@ResponseBody
	public String Findquestion(String tqid) {
		try {
			// 判断tqid是否或者数据库是否存在
			if (tqid == null || "".equals(tqid)) {
				System.out.println("服务器错误请刷新");
				return "服务器错误请刷新";
			}
			Questions questions = questionService.findByTqid(Integer.valueOf(tqid));
			String json = JSON.toJSONString(questions, SerializerFeature.DisableCircularReferenceDetect);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙请稍后操作，谢谢合作";
		}
	}

	/**
	 * 添加分页策略到session中
	 */
	@RequestMapping("/addPaging")
	@ResponseBody
	public void addPaging(String currentPage, String alltqtitle, String tname, String url, HttpServletRequest request) {
		try {
			HttpSession session = request.getSession();
			session.setAttribute("alltqtitle", alltqtitle);
			session.setAttribute("tname", tname);
			session.setAttribute("url", url);
			if (currentPage == "") {
			} else {
				session.setAttribute("currentPage", currentPage);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加分页策略到session中
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/getPaging")
	@ResponseBody
	public String getPaging(HttpServletRequest request) {
		Map<String, Object> map = new HashMap<>();
		try {
			map = questionService.getPaging(request, map);
			String json = JSON.toJSONString(map, SerializerFeature.DisableCircularReferenceDetect);
			return json;
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙！";
		}
	}

	/**
	 * 把提示信息加入session
	 */
	@RequestMapping("/addmessage")
	public void Addmessage(HttpServletRequest request, String message) {
		try {
			if (message != null) {
				request.getSession().setAttribute("message", message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从session获得提示信息
	 */
	@RequestMapping("/getmessage")
	@ResponseBody
	public String getmessage(HttpServletRequest request) {
		String message = (String) request.getSession().getAttribute("message");
		try {
			if (message != null) {
				return message;
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙！";
		}
	}

	public boolean QuestionskHasNullOrNot(Questions questions) {
		if (questions.getOptiona() == null || "".equals(questions.getOptiona())) {
			return false;
		} else if (questions.getOptionb() == null || "".equals(questions.getOptionb())) {
			return false;
		} else if (questions.getOptionc() == null || "".equals(questions.getOptionc())) {
			return false;
		} else if (questions.getOptiond() == null || "".equals(questions.getOptiond())) {
			return false;
		} else if (questions.getTqtitle() == null || "".equals(questions.getTqtitle())) {
			return false;
		} else if (questions.getQuestion() == null || "".equals(questions.getQuestion())) {
			return false;
		} else if (questions.getType().getTname() == null || "".equals(questions.getType().getTname())) {
			return false;
		}
		return true;
	}

	public boolean QuestionsIsToLongOrNot(Questions questions) {
		if (questions.getOptiona().length() >= 255) {
			return false;
		} else if (questions.getOptionb().length() >= 255) {
			return false;
		} else if (questions.getOptionc().length() >= 255) {
			return false;
		} else if (questions.getOptiond().length() >= 255) {
			return false;
		} else if (questions.getQuestion().length() >= 255) {
			return false;
		} else if (questions.getTqtitle().length() >= 1000) {
			return false;
		}
		return true;
	}

	// 把uri放入PageBean
	private void setPageBean(HttpServletRequest request, PageBean pb) {
		String url = request.getRequestURI();
		pb.setUrl(url);
	}

}
