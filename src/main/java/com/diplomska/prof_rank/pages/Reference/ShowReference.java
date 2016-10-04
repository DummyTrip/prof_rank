package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.PageRenderLinkSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 03-Oct-16.
 */
public class ShowReference {

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

    @Property
    String referenceName;

    @ActivationRequestParameter(value = "name")
    private String referenceNameQueryString;

    @Inject
    private PageRenderLinkSource pageRenderLinkSource;

    public List<ReferenceInstance> getReferenceInstances() {
        User user = userHibernate.getById(Long.valueOf(1));
        if (user != null) {
//            return referenceInstanceHibernate.getByReferenceAndUser(reference, user);
            List<ReferenceInstance> referenceInstances = referenceInstanceHibernate.getByReference(reference);
            return referenceInstances;
        } else {
            return new ArrayList<ReferenceInstance>();
        }
//        return referenceInstanceHibernate.getByReference(reference);
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

    public Link set(String referenceName) {
        this.referenceNameQueryString = referenceName;

        return pageRenderLinkSource.createPageRenderLink(this.getClass());
    }

    public Object onSuccessFromForm() {
        Link link = this.set(referenceName);

        return link;
    }

    void setupRender() throws Exception {
        this.reference = referenceHibernate.getById(referenceId);
        this.referenceName = referenceNameQueryString;
    }

}
