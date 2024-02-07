package com.eatwhere.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionEvent {
    
    private Long senderUserId;
    private String senderName;
    private Long sessionId;
    private SessionEventType type;

    public SessionEvent(Long senderUserId, String senderName, Long sessionId, SessionEventType type) {
        this.senderUserId = senderUserId;
        this.senderName = senderName;
        this.sessionId = sessionId;
        this.type = type;
    }

}
