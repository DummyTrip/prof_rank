package com.diplomska.prof_rank.pages.Institution;

import com.diplomska.prof_rank.entities.Role;
import com.diplomska.prof_rank.entities.Institution;
import com.diplomska.prof_rank.services.RoleHibernate;
import com.diplomska.prof_rank.services.InstitutionHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.diplomska.prof_rank.pages.Institution.Index;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class CreateInstitution {
    @Property
    private Institution institution;

    @Inject
    private InstitutionHibernate institutionHibernate;

    @Inject
    private RoleHibernate roleHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            institution = new Institution();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        institution = new Institution();
    }

    @CommitAfter
    Object onSuccess() {
        institutionHibernate.store(institution);

        return index;
    }
}
