package com.diplomska.prof_rank.pages.ReferenceType;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.AttributeReferenceType;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.services.AttributeHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
public class Show {
    @Persist
    @Property
    private Long referenceTypeId;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    private AttributeHibernate attributeHibernate;

    @Property
    private ReferenceType referenceType;

    @Property
    private Attribute attribute;

    @Property
    private AttributeReferenceType attributeReferenceType;

    @Property
    private BeanModel<AttributeReferenceType> attributeReferenceTypeBeanModel;

    @Property
    private BeanModel<Attribute> attributeBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Attribute> getAttributes() {
        return attributeHibernate.getAll();
    }

    public List<AttributeReferenceType> getAttributeReferenceTypes() {
        List<AttributeReferenceType> attributeReferenceTypes = new ArrayList<AttributeReferenceType>();
        List<ReferenceType> allReferenceTypes = referenceTypeHibernate.getAll();

        for (ReferenceType ref : allReferenceTypes) {
            attributeReferenceTypes.addAll(ref.getAttributeReferenceTypes());
        }

        return attributeReferenceTypes;
    }

    void onActivate(Long referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
    }

    Long passivate() {
        return referenceTypeId;
    }

    void setupRender() throws Exception {
        referenceType = referenceTypeHibernate.getById(referenceTypeId);

        if (referenceType == null) {
            throw new Exception("ReferenceType " + referenceTypeId + " does not exist.");
        }

        attributeBeanModel = beanModelSource.createDisplayModel(Attribute.class, messages);
        attributeBeanModel.include("name", "inputType");
        attributeBeanModel.add("add", null);

        attributeReferenceTypeBeanModel = beanModelSource.createDisplayModel(AttributeReferenceType.class, messages);
        attributeReferenceTypeBeanModel.add("referenceType", pcs.create(AttributeReferenceType.class, "referenceType"));
        attributeReferenceTypeBeanModel.add("attribute", pcs.create(AttributeReferenceType.class, "attribute"));
    }

    @CommitAfter
    void onActionFromAdd(Long attributeId) {
        referenceType = referenceTypeHibernate.getById(referenceTypeId);

        attribute = attributeHibernate.getById(attributeId);
        referenceTypeHibernate.setAttribute(referenceType, attribute);
    }
}
