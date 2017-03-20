package com.wuxianedu.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wuxianedu.dao.type.TypeRepository;
import com.wuxianedu.domain.type.Type;
import com.wuxianedu.service.TypeService;

@Service
public class TypeServiceimpl implements TypeService {

	@Autowired
	TypeRepository typeRepository;

	@Override
	public List<Type> findall() {
		return typeRepository.findAll();
	}

	@Override
	public Type findByTname(String tname) {
		return typeRepository.findByTname(tname);
	}

}
