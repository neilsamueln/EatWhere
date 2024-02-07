package com.eatwhere.event;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SessionEndEvent extends SessionEvent {
    
    private String restaurant;

    public SessionEndEvent(Long senderUserId, String senderName, Long sessionId, SessionEventType type, String restaurant) {
        super(senderUserId, senderName, sessionId, type);
        this.restaurant = restaurant;
    }
    
}
