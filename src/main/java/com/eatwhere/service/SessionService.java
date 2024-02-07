package com.eatwhere.service;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.eatwhere.exception.ApiException;
import com.eatwhere.exception.ApiException.ApiExceptionType;
import com.eatwhere.model.Session;
import com.eatwhere.model.SessionState;
import com.eatwhere.model.SessionUser;
import com.eatwhere.model.SessionUserState;
import com.eatwhere.model.User;
import com.eatwhere.repository.SessionRepository;
import com.eatwhere.repository.SessionUserRepository;
import com.eatwhere.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class SessionService {
    
    private final SessionRepository sessionRepository;
    private final SessionUserRepository sessionUserRepository;
    private final UserRepository userRepository;

    public Session findSession(Long id) throws ApiException {
        return sessionRepository.findById(id).orElseThrow(() -> new ApiException(ApiExceptionType.NOT_FOUND));
    }

    public List<Session> findSessions(SessionState sessionState, Long userId, SessionUserState sessionUserState) {
        return sessionRepository.findBySessionStateAndSessionUserIdAndSessionUserState(sessionState, userId, sessionUserState);
    }

    public List<Session> findSessions(Long userId, SessionUserState sessionUserState) {
        return sessionRepository.findBySessionUserIdAndSessionUserState(userId, sessionUserState);
    }

    public Session createSession(Long ownerUserId) throws ApiException {
        final User owner = userRepository.findById(ownerUserId).orElseThrow(() -> new ApiException(ApiExceptionType.NOT_FOUND));
        final Session session = Session.builder()
            .owner(owner)
            .state(SessionState.STARTED)
            .startDate(new Date())
            .build();

        session.addSessionUser(owner, SessionUserState.JOINED);

        return sessionRepository.save(session);
    }

    public Session endSession(Long sessionId, Long userId) throws ApiException {
        final Session session = findSession(sessionId);

        // only the session owner can end the session
        if (session.getOwner().getId() != userId) {
            throw new ApiException(ApiExceptionType.ACCESS_DENIED);
        }

        session.setState(SessionState.ENDED);
        session.setEndDate(new Date());
        session.setRestaurant(pickRandomRestaurant(session.getSessionUsers()));

        return sessionRepository.save(session);
    }

    public boolean addInvitedUserToSession(Long sessionId, Long senderUserId, Long userId) throws ApiException {
        final Session session = findSession(sessionId);

        // only the session owner can end the session
        if (session.getOwner().getId() != senderUserId) {
            throw new ApiException(ApiExceptionType.ACCESS_DENIED);
        }

        // do not add invited users if the session is already ended
        if (SessionState.ENDED.equals(session.getState())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }
        
        final SessionUser existingSessionUser = sessionUserRepository.findBySessionIdAndUserId(sessionId, userId);
        
        // do not add if the user is already in the session
        if (existingSessionUser == null) {
            final User user = userRepository.findById(userId).orElseThrow(() -> new ApiException(ApiExceptionType.NOT_FOUND));
            final SessionUser sessionUser = SessionUser.builder()
                .user(user)
                .session(session)
                .state(SessionUserState.INVITED)
                .build();

            sessionUserRepository.save(sessionUser);

            return true;
        }

        return false;
    }

    public void updateUserToJoinedSession(Long sessionId, Long userId) throws ApiException {
        final SessionUser sessionUser = sessionUserRepository.findBySessionIdAndUserId(sessionId, userId);
        if (sessionUser == null) {
            throw new ApiException(ApiExceptionType.NOT_FOUND);
        } 
        
        final Session session = findSession(sessionId);

        // do not let users join if the session is already ended
        if (SessionState.ENDED.equals(session.getState())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        sessionUser.setState(SessionUserState.JOINED);
        sessionUserRepository.save(sessionUser);
    }

    public void updateUserRestaurant(Long sessionId, Long userId, String restaurant) throws ApiException {
        final SessionUser sessionUser = sessionUserRepository.findBySessionIdAndUserId(sessionId, userId);
        if (sessionUser == null) {
            throw new ApiException(ApiExceptionType.NOT_FOUND);
        } 
        
        if (StringUtils.isBlank(sessionUser.getRestaurant())) {
            sessionUser.setRestaurant(restaurant);
            sessionUserRepository.save(sessionUser);
        }

    }

    private String pickRandomRestaurant(List<SessionUser> sessionUsers) {

        // only pick from users who submitted a restaurant
        final List<String> restaurants = sessionUsers.stream()
            .filter(su -> StringUtils.isNotBlank(su.getRestaurant()))
            .map(su -> su.getRestaurant())
            .collect(Collectors.toList());

        if (restaurants.size() > 0) {
            final Random random = new Random();
            return restaurants.get(random.nextInt(restaurants.size()));
        }
        
        return "(no restaurant submitted)";
    }

}
