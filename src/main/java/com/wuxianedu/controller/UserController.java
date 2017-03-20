package com.wuxianedu.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wuxianedu.dao.user.UserRepository;
import com.wuxianedu.domain.questions.UpDownLoadPosition;
import com.wuxianedu.domain.user.User;
import com.wuxianedu.utils.md5.MD5Util;
@Controller
@RequestMapping("/login")
@EnableAutoConfiguration
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@RequestMapping("/showlogin")
	public String index2(HttpServletRequest request) {
		return "/jsps/user/login";
	}
	

	@RequestMapping("/login")
	@ResponseBody
	public String login(String username, String password, HttpServletRequest request) {
		if (username == null) {
			return "请输入用户名";
		}
		if (password == null) {
			return "请输入密码";
		}
		User user = userRepository.findByUsername(username);
		System.out.println(user);

		if (user == null) {
			return "你输入的密码和账户名不匹配，是否忘记密码或忘记会员名";
		} else if (!MD5Util.string2MD5(password).equals(user.getPassword())) {
			return "你输入的密码和账户名不匹配，是否忘记密码或忘记会员名";
		} 
		request.getSession().setAttribute("user", user);
//		创建一个文件是否下载结束的字段
		request.getSession().setAttribute("finalUpLoadOrNot", "true");
		UpDownLoadPosition upDownLoadPosition=new UpDownLoadPosition();
		upDownLoadPosition.setBeginUpLoadOrOnt("begin");
//		创建一个文件是否上传结束的字段
		request.getSession().setAttribute("UpDownLoadPosition", upDownLoadPosition);
		return "true";
	}

	@RequestMapping("/leavelogin")
	@ResponseBody
	public String Leavelogin(HttpServletRequest request) {
		HttpSession session = request.getSession();
		try {
			if (session!=null) {
				session.invalidate();
			}
			return "成功退出";
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙请稍后";
		}
	}
	@RequestMapping("/hasUserOrNot")
	@ResponseBody
	public String hasUserOrNot(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		try {
			if (user!=null) {
				return "true";
			}
			return "false";
		} catch (Exception e) {
			e.printStackTrace();
			return "服务器正忙请稍后";
		}
	}

	@RequestMapping("/getUserByUsername")
	@ResponseBody
	public String getUserByUsername(String username) {
		if (userRepository.findByUsername(username) != null) {
			return "true";
		} else {
			return "false";
		}
	}



	@RequestMapping("/findusername")
	@ResponseBody
	public String Findusername(HttpServletRequest request) {
		User user = (User) request.getSession().getAttribute("user");
		if (user != null) {
			return user.getUsername();
		} else {
			return "请登录！";
		}

	}

}