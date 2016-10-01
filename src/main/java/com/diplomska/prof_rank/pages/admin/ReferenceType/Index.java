package com.diplomska.prof_rank.pages.admin.ReferenceType;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.ReferenceType;
import com.diplomska.prof_rank.services.AttributeHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 24-Sep-16.
 */
public class Index {
    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    private AttributeHibernate attributeHibernate;

    @Property
    private ReferenceType referenceType;

    @Property
    private Attribute attribute;

    @Property
    private BeanModel<ReferenceType> referenceTypeBeanModel;

    @Property
    private BeanModel<Attribute> attributeBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<ReferenceType> getReferenceTypes() {
        return referenceTypeHibernate.getAll();
    }

    public List<Attribute> getAttributes() {
        return attributeHibernate.getAll();
    }

    void setupRender() {
        referenceTypeBeanModel = beanModelSource.createDisplayModel(ReferenceType.class, messages);
        referenceTypeBeanModel.include("name");
        referenceTypeBeanModel.add("show", null);
        referenceTypeBeanModel.add("edit", null);
        referenceTypeBeanModel.add("delete", null);

        attributeBeanModel = beanModelSource.createDisplayModel(Attribute.class, messages);
        attributeBeanModel.include("name", "inputType");
        attributeBeanModel.add("add", null);
    }

    @CommitAfter
    void onActionFromDelete(Long referenceTypeId) {
        referenceType = referenceTypeHibernate.getById(referenceTypeId);
        referenceTypeHibernate.delete(referenceType);
    }
}
