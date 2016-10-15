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
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.SelectModelFactory;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import java.util.*;

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

    @Persist
    @Property
    List<Attribute> attributes;

    public boolean isTextInput() {
        return attribute.getInputType().equals("text") ? true :false;
    }

    public boolean isNumAttributes() {
        return attributes.size() > 0 ? true :false;
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
            testMap = null;
            attributes = null;
            oldReferenceId = referenceId;
        }

        this.reference = referenceHibernate.getById(referenceId);

        if (attributes == null) {
            attributes = referenceHibernate.getAttributeValues(this.reference);
        }

        if (testMap == null) {
            testMap = new HashMap<String, String>();

            for (Attribute attribute : attributes) {
                testMap.put(String.valueOf(attribute.getId()), "");
            }
        }

        if (attributeSelectModel == null) {
            attributeSelectModel = selectModelFactory.create(getNewAttributes(), "name");
        }

    }


    @CommitAfter
    @OnEvent(component = "save", value = "selected")
    Object saveReference() {
        referenceInstance = new ReferenceInstance();
        referenceInstance.setReference(reference);
        referenceInstanceHibernate.store(referenceInstance);

        referenceInstanceHibernate.updateAttributeReferenceInstances(referenceInstance, testMap, attributes);

        testMap = null;
        attributes = null;

        return index;
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


    @OnEvent(component = "addAttribute", value = "selected")
    void addAttribute() {
        if (newAttribute != null) {
            String id = String.valueOf(newAttribute.getId());
            if (!testMap.containsKey(id)) {
                attributes.add(newAttribute);
                testMap.put(id, "");
            }
        }

        if (request.isXHR()) {
            ajaxResponseRenderer.addRender(newAttributesZone);
        }
    }

    Object onActionFromCancel() {
        testMap = null;
        attributes = null;

        return index;
    }

    @CommitAfter
    @OnEvent(component = "delete", value = "selected")
    public void delete(Long attributeId) {
        for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext(); ) {
            Long id = iterator.next().getId();
            if (id.equals(attributeId)) {
                iterator.remove();
            }
        }

        testMap.remove(String.valueOf(attributeId));
    }
}
