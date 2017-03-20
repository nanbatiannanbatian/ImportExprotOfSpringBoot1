package com.wuxianedu.service;

import java.util.List;

import com.wuxianedu.domain.type.Type;

public interface TypeService {

	public List<Type> findall();

	public Type findByTname(String tname);

}
