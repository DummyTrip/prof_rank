package com.diplomska.prof_rank.pages;

import com.diplomska.prof_rank.entities.Institution;
import com.diplomska.prof_rank.entities.User;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;

import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
public class Temp {
    @Property
    private User tmp;

    @Inject
    Session session;

    public List<User> getTemp() {
        return session.createCriteria(User.class).list();
    }
}
