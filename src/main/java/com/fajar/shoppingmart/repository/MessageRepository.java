package com.fajar.shoppingmart.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fajar.shoppingmart.entity.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

}
