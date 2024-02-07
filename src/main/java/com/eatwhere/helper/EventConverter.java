package com.eatwhere.helper;

import java.util.ArrayList;
import java.util.List;

import com.eatwhere.event.SessionEventType;
import com.eatwhere.event.SessionSubscribeEvent;
import com.eatwhere.event.SessionUserPayload;
import com.eatwhere.event.UserEventType;
import com.eatwhere.event.UserSendSessionInviteEvent;
import com.eatwhere.event.UserSendSessionJoinEvent;
import com.eatwhere.event.UserSessionInvite;
import com.eatwhere.event.UserSessionJoin;
import com.eatwhere.model.Session;
import com.eatwhere.model.SessionUser;

public class EventConverter {
    
    public static UserSendSessionInviteEvent toUserInviteEvent(Long userId, List<Session> sessions) {
        final List<UserSessionInvite> invitedSessions = new ArrayList<>();
        for (Session session : sessions) {
            final UserSessionInvite invite = new UserSessionInvite(session.getId(), session.getOwner().getName());
            invitedSessions.add(invite);
        }

        final UserSendSessionInviteEvent event = new UserSendSessionInviteEvent(userId, UserEventType.INVITE, invitedSessions);
        return event;
    }

    public static UserSendSessionJoinEvent toUserJoinEvent(Long userId, List<Session> sessions) {
        final List<UserSessionJoin> joinedSessions = new ArrayList<>();
        for (Session session : sessions) {
            final UserSessionJoin join = new UserSessionJoin(session.getId(), session.getOwner().getId(), session.getOwner().getName(), session.getState(), session.getRestaurant());
            joinedSessions.add(join);
        }

        final UserSendSessionJoinEvent event = new UserSendSessionJoinEvent(userId, UserEventType.JOIN, joinedSessions);
        return event;
    }

    public static SessionUserPayload toSessionUserPayload(SessionUser sessionUser) {
        return new SessionUserPayload(sessionUser.getUser().getId(), sessionUser.getUser().getName(), sessionUser.getState(), sessionUser.getRestaurant());
    }

    public static SessionSubscribeEvent toSessionSubscribeEvent(Session session) {
        final List<SessionUserPayload> sessionUsers = new ArrayList<>();
        for (SessionUser su : session.getSessionUsers()) {
            sessionUsers.add(toSessionUserPayload(su));
        }

        final SessionSubscribeEvent event = new SessionSubscribeEvent(null, null, session.getId(), SessionEventType.SESSION_SUBSCRIBE, session.getState(), sessionUsers);
        return event;
    }
    

}
