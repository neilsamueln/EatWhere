package com.eatwhere.event;

import java.util.List;

import com.eatwhere.model.SessionState;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionSubscribeEvent extends SessionEvent {
    
    private SessionState sessionState;
    private List<SessionUserPayload> sessionUsers;

    public SessionSubscribeEvent(Long senderUserId, String senderName, Long sessionId, SessionEventType type,
            SessionState sessionState, List<SessionUserPayload> sessionUsers) {
        super(senderUserId, senderName, sessionId, type);
        this.sessionState = sessionState;
        this.sessionUsers = sessionUsers;
    }

}
