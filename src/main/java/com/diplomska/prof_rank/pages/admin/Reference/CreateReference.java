package com.diplomska.prof_rank.pages.admin.Reference;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.ReferenceInputTemplate;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import com.diplomska.prof_rank.services.ReferenceHibernate;
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
@AdministratorPage
public class CreateReference {
    @Property
    private Reference reference;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @InjectPage
    private Index index;

    @InjectComponent
    private Form form;

    @Property
    private ReferenceInputTemplate referenceInputTemplate;

    private List<ReferenceInputTemplate> referenceInputTemplates;

    public List<ReferenceInputTemplate> getReferenceInputTemplates() {
        return referenceTypeHibernate.getAll();
    }

    void onPrepareForRender() throws Exception {
        // If fresh start, make sure there's a Person object available.
        if (form.isValid()) {
            reference = new Reference();
        }
    }

    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        reference = new Reference();
    }

    @CommitAfter
    Object onSuccess() {
        referenceHibernate.store(reference);

        if (referenceInputTemplate != null){
            referenceHibernate.setReferenceType(reference, referenceInputTemplate);
        }

        return index;
    }
}
