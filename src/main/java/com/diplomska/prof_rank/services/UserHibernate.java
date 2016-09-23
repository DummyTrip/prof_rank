package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.User;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
public class UserHibernate {
    @Inject
    Session session;

    public void store(User user) {
        session.persist(user);
    }
}
