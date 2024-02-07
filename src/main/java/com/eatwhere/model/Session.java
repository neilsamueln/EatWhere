package com.eatwhere.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "sessions", schema = "eatwhere")
public class Session {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "owner_user_id")
    private User owner;

    private String restaurant;

    @Enumerated(EnumType.STRING)
    private SessionState state;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<SessionUser> sessionUsers = new ArrayList<>();

    public void addSessionUser(User user, SessionUserState sessionUserState) {
        if (getSessionUsers() == null) {
            setSessionUsers(new ArrayList<>());
        }

        final SessionUser sessionUser = SessionUser.builder()
            .user(user)
            .session(this)
            .state(sessionUserState)
            .build();

        getSessionUsers().add(sessionUser);
    }
}
