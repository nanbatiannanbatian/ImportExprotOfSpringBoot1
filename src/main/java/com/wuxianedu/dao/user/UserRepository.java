package com.wuxianedu.dao.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wuxianedu.domain.user.User;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByUsername(String username);

}