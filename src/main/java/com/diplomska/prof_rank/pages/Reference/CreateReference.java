package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.diplomska.prof_rank.pages.Reference.Index;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
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
    private ReferenceType referenceType;

    private List<ReferenceType> referenceTypes;

    public List<ReferenceType> getReferenceTypes() {
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

        if (referenceType != null){
            referenceHibernate.setReferenceType(reference, referenceType);
        }

        return index;
    }
}
