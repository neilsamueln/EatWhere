package com.eatwhere.controller;

import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.eatwhere.event.SessionEndEvent;
import com.eatwhere.event.SessionEvent;
import com.eatwhere.event.SessionEventType;
import com.eatwhere.event.SessionSubscribeEvent;
import com.eatwhere.event.SessionUserInviteEvent;
import com.eatwhere.event.SessionUserRestaurantSubmitEvent;
import com.eatwhere.event.UserEvent;
import com.eatwhere.event.UserSendSessionInviteEvent;
import com.eatwhere.exception.ApiException;
import com.eatwhere.exception.ApiException.ApiExceptionType;
import com.eatwhere.helper.EventConverter;
import com.eatwhere.model.Session;
import com.eatwhere.service.SessionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class SessionController {
    
    private final SessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<Session> findSession(@PathVariable Long sessionId) throws ApiException {
        return ResponseEntity.ok(sessionService.findSession(sessionId));
    }

    @SubscribeMapping("/session/{sessionId}")
    public SessionSubscribeEvent sessionSubscribe(@DestinationVariable Long sessionId) throws ApiException {
        final Session session = sessionService.findSession(sessionId);
        return EventConverter.toSessionSubscribeEvent(session);
    }

    @MessageMapping("/session/create")
    public void createSessionEvent(@Payload SessionEvent event) throws ApiException {

        if (event == null || event.getSenderUserId() == null || !SessionEventType.SESSION_CREATE.equals(event.getType())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        final Session createdSession = sessionService.createSession(event.getSenderUserId());

        // send created session to the user
        final UserEvent sendUserJoinEvent = EventConverter.toUserJoinEvent(event.getSenderUserId(), Arrays.asList(createdSession));
        messagingTemplate.convertAndSendToUser(String.valueOf(event.getSenderUserId()), "/queue/events", sendUserJoinEvent);
    }

    @MessageMapping("/session/{sessionId}/invite")
    @SendTo("/session/{sessionId}")
    public SessionUserInviteEvent SessionUserInviteEvent(@DestinationVariable Long sessionId, @Payload SessionUserInviteEvent event) throws ApiException {

        if (event == null || event.getSessionId() == null || event.getSenderUserId() == null || event.getUserId() == null || !SessionEventType.USER_INVITE.equals(event.getType())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        boolean isInvited = sessionService.addInvitedUserToSession(event.getSessionId(), event.getSenderUserId(), event.getUserId());

        if (isInvited) {
            // send an invite to the user
            final Session session = sessionService.findSession(event.getSessionId());
            final UserSendSessionInviteEvent sendUserInviteEvent = EventConverter.toUserInviteEvent(event.getUserId(), Arrays.asList(session));
            messagingTemplate.convertAndSendToUser(String.valueOf(event.getUserId()), "/queue/events", sendUserInviteEvent);
        
            // broadcast to session users that a user was invited to this session
            return event;
        }

        return null;
    }

    @MessageMapping("/session/{sessionId}/join")
    @SendTo("/session/{sessionId}")
    public SessionEvent userJoinEvent(@DestinationVariable Long sessionId, @Payload SessionEvent event) throws ApiException {

        if (event == null || event.getSessionId() == null || event.getSenderUserId() == null || !SessionEventType.USER_JOIN.equals(event.getType())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        sessionService.updateUserToJoinedSession(event.getSessionId(), event.getSenderUserId());

        // send session information to the user that joined
        final Session session = sessionService.findSession(event.getSessionId());
        final UserEvent sendUserJoinEvent = EventConverter.toUserJoinEvent(event.getSenderUserId(), Arrays.asList(session));
        messagingTemplate.convertAndSendToUser(String.valueOf(event.getSenderUserId()), "/queue/events", sendUserJoinEvent);
    
        // broadcast to session users that a user joined this session
        return event;

    }

    @MessageMapping("/session/{sessionId}/restaurant")
    @SendTo("/session/{sessionId}")
    public SessionUserRestaurantSubmitEvent userRestaurantSubmitEvent(@DestinationVariable Long sessionId, @Payload SessionUserRestaurantSubmitEvent event) throws ApiException {
        
        if (event == null || event.getSessionId() == null || event.getSenderUserId() == null || !SessionEventType.USER_RESTAURANT_SUBMIT.equals(event.getType())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        sessionService.updateUserRestaurant(event.getSessionId(), event.getSenderUserId(), event.getRestaurant());

        return event;
    }

    @MessageMapping("/session/{sessionId}/end")
    @SendTo("/session/{sessionId}")
    public SessionEndEvent sessionEndEvent(@DestinationVariable Long sessionId, @Payload SessionEndEvent event) throws ApiException {

        if (event == null || event.getSessionId() == null || event.getSenderUserId() == null || !SessionEventType.SESSION_END.equals(event.getType())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        final Session updatedSession = sessionService.endSession(event.getSessionId(), event.getSenderUserId());
        event.setRestaurant(updatedSession.getRestaurant());

        return event;
    }

}
