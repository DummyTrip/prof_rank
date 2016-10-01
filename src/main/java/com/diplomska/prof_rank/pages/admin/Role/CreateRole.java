package com.diplomska.prof_rank.pages.admin.Role;

import com.diplomska.prof_rank.entities.Role;
import com.diplomska.prof_rank.services.RoleHibernate;
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
public class CreateRole {
    @Property
    private Role role;

    @Inject
    private RoleHibernate roleHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    private List<Role> roles;

    public List<Role> getRoles() {
        return roleHibernate.getAll();
    }

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            role = new Role();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        role = new Role();
    }

    @CommitAfter
    Object onSuccess() {
        roleHibernate.store(role);

        return index;
    }
}
