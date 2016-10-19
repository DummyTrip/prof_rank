package com.diplomska.prof_rank.pages.admin.ReferenceInputTemplate;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.ReferenceInputTemplate;
import com.diplomska.prof_rank.services.ReferenceInputTemplateHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
@AdministratorPage
public class CreateReferenceInputTemplate {
    @Property
    private ReferenceInputTemplate referenceInputTemplate;

    @Inject
    private ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            referenceInputTemplate = new ReferenceInputTemplate();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        referenceInputTemplate = new ReferenceInputTemplate();
    }

    @CommitAfter
    Object onSuccess() {
        referenceInputTemplateHibernate.store(referenceInputTemplate);

        return index;
    }
}
