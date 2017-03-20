package com.wuxianedu;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SpringbootDay071Application {
	@PostConstruct
	void demo() {
		System.out.println("******BOOT demo 古力娜扎!********");
	}

	@Bean
	public MultipartConfigElement multipartConfigElement() {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		//// 设置文件大小限制 ,超了，页面会抛出异常信息，这时候就需要进行异常信息的处理了;
		factory.setMaxFileSize("200MB"); // KB,MB
		/// 设置总上传数据总大小
		factory.setMaxRequestSize("3000MB");
		// Sets the directory location where files will be stored.
		// factory.setLocation("路径地址");
		return factory.createMultipartConfig();
	}

	public static void main(String[] args) {
		SpringApplication.run(SpringbootDay071Application.class, args);
	}
}
