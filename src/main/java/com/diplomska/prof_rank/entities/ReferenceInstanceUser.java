package com.diplomska.prof_rank.entities;

import org.apache.tapestry5.beaneditor.NonVisual;

import javax.persistence.*;

/**
 * Created by Aleksandar on 27.09.2016.
 */
@Entity
public class ReferenceInstanceUser {
    private Long id;

    private ReferenceInstance referenceInstance;

    private User user;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NonVisual
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "referenceInstance_id")
    public ReferenceInstance getReferenceInstance() {
        return referenceInstance;
    }

    public void setReferenceInstance(ReferenceInstance referenceInstance) {
        this.referenceInstance = referenceInstance;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
