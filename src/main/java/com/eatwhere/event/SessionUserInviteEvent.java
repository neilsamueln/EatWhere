package com.eatwhere.event;

import com.eatwhere.model.SessionState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionUserInviteEvent extends SessionEvent {
    
    private Long userId;
    private String name;
    private SessionState sessionState;

    public SessionUserInviteEvent(Long senderUserId, String senderName, Long sessionId, SessionEventType type,
            Long userId, String name, SessionState sessionState) {
        super(senderUserId, senderName, sessionId, type);
        this.userId = userId;
        this.name = name;
        this.sessionState = sessionState;
    }

}
