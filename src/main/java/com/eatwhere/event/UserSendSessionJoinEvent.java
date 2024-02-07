package com.eatwhere.event;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSendSessionJoinEvent extends UserEvent {
    
    private List<UserSessionJoin> joinedSessions;

    public UserSendSessionJoinEvent(Long userId, UserEventType type, List<UserSessionJoin> joinedSessions) {
        super(userId, type);
        this.joinedSessions = joinedSessions;
    }

}
