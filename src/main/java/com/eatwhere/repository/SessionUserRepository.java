package com.eatwhere.repository;

import org.springframework.data.repository.CrudRepository;

import com.eatwhere.model.SessionUser;

public interface SessionUserRepository extends CrudRepository<SessionUser, Long> {
    
    SessionUser findBySessionIdAndUserId(Long sessionId, Long userId);

}
