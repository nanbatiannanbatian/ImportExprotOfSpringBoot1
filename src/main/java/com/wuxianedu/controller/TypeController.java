package com.wuxianedu.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.wuxianedu.domain.type.Type;
import com.wuxianedu.service.impl.TypeServiceimpl;

@Controller
@RequestMapping("/type")
@EnableAutoConfiguration
public class TypeController {
	@Autowired
	TypeServiceimpl typeService;

	/**
	 * 查找所有类型在网页显现出来
	 * 
	 * @return
	 */
	@RequestMapping("/findallTypeof")
	@ResponseBody
	public String FindallType() {
		List<Type> types = typeService.findall();
		String json = JSON.toJSONString(types, SerializerFeature.DisableCircularReferenceDetect);
		return json;
	}

}
