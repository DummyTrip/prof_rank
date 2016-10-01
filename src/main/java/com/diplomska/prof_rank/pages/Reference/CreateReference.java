package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.pages.*;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * Created by Aleksandar on 01-Oct-16.
 */
public class CreateReference {
    @Persist
    @Property
    private Long referenceId;

    @Persist
    @Property
    private Reference reference;

    @Inject
    ReferenceHibernate referenceHibernate;

    @InjectPage
    private com.diplomska.prof_rank.pages.Index index;

    public List<Reference> getReferences() {
        return referenceHibernate.getAll();
    }

    @Property
    private Attribute attribute;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    public List<Attribute> getAttributes() {
        return referenceHibernate.getAttributeValues(reference);
//        ReferenceType referenceType = referenceHibernate.getReferenceType(reference);
//        return referenceTypeHibernate.getAttributes(referenceType);
    }

    public boolean isTextInput() {
        return attribute.getInputType().equals("text") ? true :false;
    }

    @Property
    private String testVal;

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    void setupRender() throws Exception {
        this.reference = referenceHibernate.getById(referenceId);
    }
    
    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        reference = new Reference();
    }

    @CommitAfter
    Object onSuccess() {
        referenceHibernate.store(reference);

        return index;
    }
}
