package com.eatwhere.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.eatwhere.event.UserEvent;
import com.eatwhere.event.UserEventType;
import com.eatwhere.exception.ApiException;
import com.eatwhere.exception.ApiException.ApiExceptionType;
import com.eatwhere.helper.EventConverter;
import com.eatwhere.model.Session;
import com.eatwhere.model.SessionState;
import com.eatwhere.model.SessionUserState;
import com.eatwhere.model.User;
import com.eatwhere.service.SessionService;
import com.eatwhere.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final SessionService sessionService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @GetMapping("/users/search")
    public ResponseEntity<User> findUserByName(@RequestParam String name) throws ApiException {
        return ResponseEntity.ok(userService.findUserByName(name));
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody User user) throws ApiException {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @MessageMapping("/user/online")
    public void userOnlineEvent(@Payload UserEvent event) throws ApiException {

        if (event.getUserId() == null || !UserEventType.ONLINE.equals(event.getType())) {
            throw new ApiException(ApiExceptionType.INVALID_INPUT);
        }

        // send pending invites to the user from open sessions
        final List<Session> invitedSessions = sessionService.findSessions(SessionState.STARTED, event.getUserId(), SessionUserState.INVITED);
        final UserEvent sendUserInviteEvent = EventConverter.toUserInviteEvent(event.getUserId(), invitedSessions);
        messagingTemplate.convertAndSendToUser(String.valueOf(event.getUserId()), "/queue/events", sendUserInviteEvent);
    
        // send joined sessions to the user
        final List<Session> joinedSessions = sessionService.findSessions(event.getUserId(), SessionUserState.JOINED);
        final UserEvent sendUserJoinEvent = EventConverter.toUserJoinEvent(event.getUserId(), joinedSessions);
        messagingTemplate.convertAndSendToUser(String.valueOf(event.getUserId()), "/queue/events", sendUserJoinEvent);
    
    }

}
