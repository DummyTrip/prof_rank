package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.AttributeReferenceInstance;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.services.AttributeHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
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
 * Created by Aleksandar on 09-Oct-16.
 */
@InstructorPage
public class EditReference {
    @Persist
    @Property
    private Long referenceInstanceId;

    @Persist
    @Property
    private ReferenceInstance referenceInstance;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @InjectPage
    private com.diplomska.prof_rank.pages.Reference.ShowReference showReference;

    @Property
    private Attribute attribute;

    public List<Attribute> getAttributes() {
        return referenceInstanceHibernate.getAttributeValues(referenceInstance);
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

    void onActivate(Long referenceInstanceId) {
        this.referenceInstanceId = referenceInstanceId;
    }

    Long passivate() {
        return referenceInstanceId;
    }

    void setupRender() throws Exception {
        this.referenceInstance = referenceInstanceHibernate.getById(referenceInstanceId);
        testMap = new HashMap<String, String>();

        for (AttributeReferenceInstance ari: referenceInstance.getAttributeReferenceInstances()) {
            testMap.put(String.valueOf(ari.getAttribute().getId()), ari.getValue());
        }
    }

//    @CommitAfter
//    void onPrepareForSubmit() throws Exception {
//        // Instantiate a Person for the form data to overlay.
////        referenceInstance = new ReferenceInstance();
////        referenceInstance.setReferenceInstance(referenceInstance);
//        referenceInstanceHibernate.store(referenceInstance);
//    }

    @CommitAfter
    Object onSuccess() {
        referenceInstance = referenceInstanceHibernate.getById(referenceInstanceId);

        for (String attributeId : testMap.keySet()) {
            Attribute attribute = attributeHibernate.getById(Long.valueOf(attributeId));

            referenceInstanceHibernate.setAttributeValue(referenceInstance, attribute, testMap.get(attributeId));
        }

        return showReference;
    }
}
