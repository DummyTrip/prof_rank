package com.diplomska.prof_rank.pages.User;

import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.Role;
import com.diplomska.prof_rank.entities.User;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.RoleHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.SelectModelFactory;

import com.diplomska.prof_rank.pages.User.Index;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class EditUser {
    @Persist
    private Long userId;
    
    @Property
    private User user;
    
    @InjectComponent
    private Form form;
    
    @Inject
    private UserHibernate userHibernate;
    
    @Inject
    private RoleHibernate roleHibernate;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @InjectPage
    private Index index;
    
    @Property
    private SelectModel roleSelectModel;

    @Property
    private SelectModel referenceSelectModel;
    
    @Inject
    SelectModelFactory selectModelFactory;
    
    void setupRender() {
        List<Role> roles = roleHibernate.getAll();
        List<Reference> references = referenceHibernate.getAll();

        roleSelectModel = selectModelFactory.create(roles, "name");
        referenceSelectModel = selectModelFactory.create(references, "name");
    }

//    @Property
//    private Role role;
//    private List<Role> roles;
//
//    public List<Role> getRoles() {
//        return roleHibernate.getAll();
//    }
    
    void onActivate(Long userId) {
        this.userId = userId;
    }
    
    Long passivate() {
        return userId;
    }

    void onPrepareForRender() throws Exception {
        if (form.isValid()) {
            user = findUser(userId);

            if (user == null) {
                throw new Exception("User " + userId + " does not exist.");
            }
        }
    }

    void onPrepareForSubmit() {
        user = findUser(userId);

        if (user == null) {
            form.recordError("Person has been deleted by another process.");
            // Instantiate an empty user to avoid NPE in the BeanEditForm.
            user = new User();
        }
    }

    void onValidateFromForm() {
        if (form.getHasErrors()) {
            // server-side error
            return;
        }
    }


    @CommitAfter
    Object onSuccess() {
//        user.setUloga(uloga);
        userHibernate.update(user);

        return index;
    }

    void onRefresh() {
        // By doing nothing the page will be displayed afresh.
    }

    private User findUser(Long userId) {
        User user = userHibernate.getById(userId);

        if (user == null) {
            throw new IllegalStateException("No data in database.");
        }

        return user;
    }
}
