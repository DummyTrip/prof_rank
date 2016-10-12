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
import org.apache.tapestry5.SelectModel;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import java.util.ArrayList;
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
    private Long oldReferenceInstanceId;

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
        List<Attribute> attributes = new ArrayList<Attribute>();
        for (String attributeId : testMap.keySet()) {
            attributes.add(attributeHibernate.getById(Long.valueOf(attributeId)));
        }
        return attributes;
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
        if (!referenceInstanceId.equals(oldReferenceInstanceId)) {
            testMap = null;
            oldReferenceInstanceId = referenceInstanceId;
        }

        this.referenceInstance = referenceInstanceHibernate.getById(referenceInstanceId);
        if (testMap == null) {
            testMap = new HashMap<String, String>();

            for (AttributeReferenceInstance ari: referenceInstance.getAttributeReferenceInstances()) {
                testMap.put(String.valueOf(ari.getAttribute().getId()), ari.getValue());
            }
        }

        if (attributeSelectModel == null) {
            attributeSelectModel = selectModelFactory.create(getNewAttributes(), "name");
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
    Object onSuccessFromForm() {
        referenceInstance = referenceInstanceHibernate.getById(referenceInstanceId);

        for (String attributeId : testMap.keySet()) {
            Attribute attribute = attributeHibernate.getById(Long.valueOf(attributeId));

            referenceInstanceHibernate.setAttributeValue(referenceInstance, attribute, testMap.get(attributeId));
        }

        testMap = null;

        return showReference;
    }





    @Property
    Attribute newAttribute;

    public List<Attribute> getNewAttributes() {
        return attributeHibernate.getAll();
    }

    public boolean isTestMapPopulated() {
        return getSize() > 0 ? true : false;
    }

    public Integer getSize() {
        if (testMap != null) {
            return testMap.keySet().size();
        }
        return 0;
    }

    @Inject
    private Request request;

    @InjectComponent
    private Zone newAttributesZone;

    @Property
    private SelectModel attributeSelectModel;

    @Inject
    SelectModelFactory selectModelFactory;

    @Inject
    AjaxResponseRenderer ajaxResponseRenderer;

    @InjectComponent
    Form testform;


    void onSuccessFromTestform() {
        if (newAttribute != null) {
            testMap.put(String.valueOf(newAttribute.getId()), "");
        }

        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(newAttributesZone);
        }
    }

    void onActionFromCancel() {
        testMap = null;
    }

}
