package com.wuxianedu.dao.type;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.wuxianedu.domain.type.Type;

public interface TypeRepository extends JpaRepository<Type, Integer>{

	 Type findByTname(String tname);
	
	 @Query(value = "FROM Type t")
		List<Type> findAll();
}
