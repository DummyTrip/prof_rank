package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.List;

/**
 * Created by Aleksandar on 01-Oct-16.
 */
public class Index {
    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    UserHibernate userHibernate;

    @Persist
    @Property
    Reference reference;

    @Property
    ReferenceInstance referenceInstance;

    @Property
    AttributeReferenceInstance ari;

    @Persist
    @Property
    Long referenceId;

    public List<ReferenceInstance> getReferenceInstances() {
        User user = userHibernate.getById(Long.valueOf(3));

//        return referenceInstanceHibernate.getByReferenceAndUser(reference, user);
        return referenceInstanceHibernate.getByReference(reference);
    }

    public String getDisplayName() {
        String displayName = "";
        List<AttributeReferenceInstance> attributeReferenceInstances = referenceInstance.getAttributeReferenceInstances();
        List<Attribute> attributes = referenceInstanceHibernate.getAttributeValues(referenceInstance);

        for (AttributeReferenceInstance attributeReferenceInstance : attributeReferenceInstances) {
            String attributeName = attributeReferenceInstance.getAttribute().getName();
            if (attributeName.equals("Наслов") ||
                    attributeName.equals("Предмет") ||
                    attributeName.equals("Име на проектот") ||
                    attributeName.startsWith("Период") ||
                    attributeName.equals("Година")) {
                if (displayName.length() > 0) {
                    displayName += ", ";
                }
                displayName += attributeReferenceInstance.getValue();
            }
        }

        return displayName;
    }

    public List<AttributeReferenceInstance> getAttributeValues() {
        return referenceInstance.getAttributeReferenceInstances();
    }

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    void setupRender() throws Exception {
        this.reference = referenceHibernate.getById(referenceId);
    }
}
