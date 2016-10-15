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
    private com.diplomska.prof_rank.pages.Index index;

    @InjectPage
    private com.diplomska.prof_rank.pages.Reference.ShowReference showReference;

    @Property
    private Attribute attribute;

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

    void onActivate(Long referenceInstanceId) {
        this.referenceInstanceId = referenceInstanceId;
    }

    Long passivate() {
        return referenceInstanceId;
    }

    void setupRender() throws Exception {
        if (!referenceInstanceId.equals(oldReferenceInstanceId)) {
            testMap = null;
            attributes = null;
            oldReferenceInstanceId = referenceInstanceId;
        }

        this.referenceInstance = referenceInstanceHibernate.getById(referenceInstanceId);

        if (testMap == null) {
            testMap = new HashMap<String, String>();
            attributes = new ArrayList<Attribute>();

            for (AttributeReferenceInstance ari: referenceInstanceHibernate.getSortedAttributeReferenceInstance(referenceInstance)) {
                attributes.add(ari.getAttribute());
                testMap.put(String.valueOf(ari.getAttribute().getId()), ari.getValue());
            }
        }

        if (attributeSelectModel == null) {
            attributeSelectModel = selectModelFactory.create(getNewAttributes(), "name");
        }
    }

    @CommitAfter
    @OnEvent(component = "save", value = "selected")
    Object saveReference() {
        referenceInstance = referenceInstanceHibernate.getById(referenceInstanceId);

        referenceInstanceHibernate.updateAttributeReferenceInstances(referenceInstance, testMap, attributes);

        testMap = null;
        attributes = null;

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
//        Attribute attribute = attributeHibernate.getById(attributeId);
//        referenceInstance = referenceInstanceHibernate.getById(referenceInstanceId);
//
//        referenceInstanceHibernate.deleteAttribute(referenceInstance, attribute);

        testMap.remove(String.valueOf(attributeId));
        for (Iterator<Attribute> iterator = attributes.iterator(); iterator.hasNext(); ) {
            Long id = iterator.next().getId();
            if (id.equals(attributeId)) {
                iterator.remove();
            }
        }
    }
}
