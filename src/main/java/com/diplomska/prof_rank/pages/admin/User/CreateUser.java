package com.diplomska.prof_rank.pages.admin.User;

import com.diplomska.prof_rank.entities.Role;
import com.diplomska.prof_rank.entities.User;
import com.diplomska.prof_rank.services.RoleHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class CreateUser {
    @Property
    private User user;

    @Inject
    private UserHibernate userHibernate;

    @Inject
    private RoleHibernate roleHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    @Property
    private Role role;

    private List<Role> roles;

    public List<Role> getRoles() {
        return roleHibernate.getAll();
    }

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            user = new User();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        user = new User();
    }

    @CommitAfter
    Object onSuccess() {
        userHibernate.store(user);

        if (role != null){
            userHibernate.setRole(user, role);
        }

        return index;
    }
}
