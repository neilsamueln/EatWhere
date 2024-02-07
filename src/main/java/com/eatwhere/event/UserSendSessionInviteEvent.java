package com.eatwhere.event;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSendSessionInviteEvent extends UserEvent {
    
    private List<UserSessionInvite> invitedSessions;

    public UserSendSessionInviteEvent(Long userId, UserEventType type, List<UserSessionInvite> invitedSessions) {
        super(userId, type);
        this.invitedSessions = invitedSessions;
    }

}
