package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.AttributeReferenceInstance;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.pages.*;
import com.diplomska.prof_rank.services.AttributeHibernate;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Property
    private ReferenceInstance referenceInstance;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

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

    public boolean isNumAttributes() {
        return getAttributes().size() > 0 ? true :false;
    }

    @Property
    private String testVal;

    @Property
    private Integer loopIndex;

    @Persist
    @Property
    private Map<String, String> testMap;

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    void setupRender() throws Exception {
        this.reference = referenceHibernate.getById(referenceId);
        testMap = new HashMap<String, String>();
    }

    @CommitAfter
    void onPrepareForSubmit() throws Exception {
        // Instantiate a Person for the form data to overlay.
        referenceInstance = new ReferenceInstance();
        referenceInstance.setReference(reference);
        referenceInstanceHibernate.store(referenceInstance);
    }

    @CommitAfter
    Object onSuccess() {
        for (String attributeId : testMap.keySet()) {
            Attribute attribute = attributeHibernate.getById(Long.valueOf(attributeId));

            referenceInstanceHibernate.setAttributeValue(referenceInstance, attribute, testMap.get(attributeId));
        }

        return index;
    }
}
