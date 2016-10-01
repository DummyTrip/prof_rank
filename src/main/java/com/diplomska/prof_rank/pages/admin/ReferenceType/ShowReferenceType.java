package com.diplomska.prof_rank.pages.admin.ReferenceType;

import com.diplomska.prof_rank.entities.Attribute;
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
public class ShowReferenceType {
    @Persist
    @Property
    private Long referenceTypeId;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    private AttributeHibernate attributeHibernate;

    @Persist
    @Property
    private ReferenceType referenceType;

    @Property
    private Attribute addAttribute;

    @Property
    private Attribute attribute;

    @Property
    private BeanModel<Attribute> attributeBeanModel;

    @Property
    private BeanModel<Attribute> addAttributeBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Attribute> getAddAttributes() {
        return attributeHibernate.getAll();
    }

    public List<Attribute> getAttributes() {
        List<Attribute> attributeReferenceTypes = new ArrayList<Attribute>();

        attributeReferenceTypes.addAll(referenceTypeHibernate.getAttributes(referenceType));

        return attributeReferenceTypes;
    }

    void onActivate(Long referenceTypeId) {
        this.referenceTypeId = referenceTypeId;
    }

    Long passivate() {
        return referenceTypeId;
    }

    void setupRender() throws Exception {
        this.referenceType = referenceTypeHibernate.getById(referenceTypeId);

        if (referenceType == null) {
            throw new Exception("ReferenceType " + referenceTypeId + " does not exist.");
        }

        attributeBeanModel = beanModelSource.createDisplayModel(Attribute.class, messages);
        attributeBeanModel.add("attributeName", pcs.create(Attribute.class, "name"));
        attributeBeanModel.add("attributeInputType", pcs.create(Attribute.class, "inputType"));
        attributeBeanModel.add("delete", null);

        addAttributeBeanModel = beanModelSource.createDisplayModel(Attribute.class, messages);
        addAttributeBeanModel.include("name", "inputType");
        addAttributeBeanModel.add("add", null);
    }

    @CommitAfter
    void onActionFromAdd(Long attributeId) {
        attribute = attributeHibernate.getById(attributeId);

        referenceTypeHibernate.setAttribute(referenceType, attribute);
    }

    @CommitAfter
    void onActionFromDelete(Long attributeId) {
        Attribute attr = attributeHibernate.getById(attributeId);

        referenceTypeHibernate.deleteAttribute(referenceType, attr);
    }
}
