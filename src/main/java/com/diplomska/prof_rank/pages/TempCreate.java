package com.diplomska.prof_rank.pages;

import com.diplomska.prof_rank.entities.User;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.BeanEditForm;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
public class TempCreate {
    @Property
    private User temp;

    @InjectPage
    private Temp tempPage;

    @InjectComponent
    private BeanEditForm form;

    @Inject
    UserHibernate userHibernate;

//    void onPrepareForRender() throws Exception {
//        // If fresh start, make sure there's a User object available.
//        if (form.isValid()) {
//            temp = new User();
//        }
//    }
//
//    void onPrepareForSubmit() throws Exception {
//        // Instantiate a User for the form data to overlay.
//        temp = new User();
//    }

    @CommitAfter
    Object onSuccess() {
//        session.persist(temp);
        userHibernate.store(temp);

        return tempPage;
    }
}
