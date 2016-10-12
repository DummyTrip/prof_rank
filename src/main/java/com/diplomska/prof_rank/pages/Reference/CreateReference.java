package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.AttributeReferenceInstance;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.pages.*;
import com.diplomska.prof_rank.services.AttributeHibernate;
import com.diplomska.prof_rank.services.ReferenceHibernate;
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
 * Created by Aleksandar on 01-Oct-16.
 */
@InstructorPage
public class CreateReference {
    @Persist
    @Property
    private Long referenceId;

    @Persist
    @Property
    private Long oldReferenceId;

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

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    void setupRender() throws Exception {
        if (!referenceId.equals(oldReferenceId)) {
            clearPersistetObjects();
            oldReferenceId = referenceId;
        }

        this.reference = referenceHibernate.getById(referenceId);
        if (testMap == null) {
            testMap = new HashMap<String, String>();

            List<Attribute> attributes = referenceHibernate.getAttributeValues(this.reference);

            for (Attribute attribute : attributes) {
                testMap.put(String.valueOf(attribute.getId()), "");
            }
        }

        List<Attribute> atts = getAtts();
        if (attributeSelectModel == null) {
            attributeSelectModel = selectModelFactory.create(atts, "name");
        }

    }

    void onSuccessFromForm() {
        if (newAttributeMap == null) {
            newAttributeMap = new HashMap<String, String>();
        }

        if (this.att != null) {
            newAttributeMap.put(String.valueOf(this.att.getId()), "");
        }

        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(newAttributesZone);
        }

    }





    @Property
    Attribute newAttribute;

    public List<Attribute> getNewAttributes() {
        List<Attribute> newAttributes = new ArrayList<Attribute>();

        for (String key: newAttributeMap.keySet()) {
            newAttributes.add(attributeHibernate.getById(Long.valueOf(key)));
        }

        return newAttributes;
    }

    @Persist
    @Property
    Attribute att;

    public List<Attribute> getAtts() {
        return attributeHibernate.getAll();
    }

    @Persist
    @Property
    Map<String, String> newAttributeMap;

    public boolean isNewAttributesPopulated() {
        return getSize() > 0 ? true : false;
    }

    public Integer getSize() {
        if (newAttributeMap != null) {
            return newAttributeMap.keySet().size();
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

    @CommitAfter
    Object onSuccessFromTestform() {
        referenceInstance = new ReferenceInstance();
        referenceInstance.setReference(reference);
        referenceInstanceHibernate.store(referenceInstance);

        for (String attributeId : newAttributeMap.keySet()) {
            testMap.put(attributeId, newAttributeMap.get(attributeId));
        }

        for (String attributeId : testMap.keySet()) {
            Attribute attribute = attributeHibernate.getById(Long.valueOf(attributeId));

            referenceInstanceHibernate.setAttributeValue(referenceInstance, attribute, testMap.get(attributeId));
        }

        clearPersistetObjects();

        return index;
    }

    void onActionFromCancel() {
        clearPersistetObjects();
    }

    private void clearPersistetObjects() {
        newAttributeMap = null;
        testMap = null;
        att = null;
    }
}
