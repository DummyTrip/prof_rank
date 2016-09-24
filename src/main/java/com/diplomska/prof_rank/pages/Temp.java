package com.diplomska.prof_rank.pages;

import com.diplomska.prof_rank.entities.Institution;
import com.diplomska.prof_rank.entities.User;
import com.diplomska.prof_rank.services.UserHibernate;
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
    UserHibernate userHibernate;

    public List<User> getTemp() {
        return userHibernate.getAll();
    }
}
