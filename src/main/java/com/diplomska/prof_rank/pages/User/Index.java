package com.diplomska.prof_rank.pages.User;

import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.User;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class Index {
    @Inject
    private UserHibernate userHibernate;

    @Property
    private User user;

    @Property
    private BeanModel<User> userBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<User> getUsers() {
        return userHibernate.getAll();
    }

    void setupRender() {
        userBeanModel = beanModelSource.createDisplayModel(User.class, messages);
        userBeanModel.include("firstName", "fatherName", "lastName", "email");
        userBeanModel.add("role", pcs.create(User.class, "role"));
        userBeanModel.add("edit", null);
        userBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long userId) {
        user = userHibernate.getById(userId);
        userHibernate.delete(user);
    }
}
