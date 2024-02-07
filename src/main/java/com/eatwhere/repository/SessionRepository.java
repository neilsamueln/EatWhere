package com.eatwhere.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.eatwhere.model.Session;
import com.eatwhere.model.SessionState;
import com.eatwhere.model.SessionUserState;

@Repository
public interface SessionRepository extends CrudRepository<Session, Long> {
    
    @Query("SELECT s FROM Session s INNER JOIN s.sessionUsers su WHERE s.state = :sessionState AND su.user.id = :userId AND su.state = :sessionUserState ORDER BY s.startDate DESC")
    List<Session> findBySessionStateAndSessionUserIdAndSessionUserState(SessionState sessionState, Long userId, SessionUserState sessionUserState);


    // @Query("SELECT s FROM Session s INNER JOIN s.sessionUsers su WHERE su.user.id = :userId")
    // List<Session> findBySessionStateAndSessionUserIdAndSessionUserState(Long userId);

    @Query("SELECT s FROM Session s INNER JOIN s.sessionUsers su WHERE su.user.id = :userId AND su.state = :sessionUserState ORDER BY s.startDate DESC")
    List<Session> findBySessionUserIdAndSessionUserState(Long userId, SessionUserState sessionUserState);

    // @Query("SELECT s FROM Session s INNER JOIN s.sessionUsers su WHERE su.user.id = :userId")
    // List<Session> findBySessionUserIdAndSessionUserState(Long userId);

}
