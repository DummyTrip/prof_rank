package com.diplomska.prof_rank.pages.ReferenceType;

import com.diplomska.prof_rank.entities.ReferenceType;
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

    @Property
    private ReferenceType referenceType;

    @Property
    private BeanModel<ReferenceType> referenceTypeBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<ReferenceType> getReferenceTypes() {
        return referenceTypeHibernate.getAll();
    }

    void setupRender() {
        referenceTypeBeanModel = beanModelSource.createDisplayModel(ReferenceType.class, messages);
        referenceTypeBeanModel.include("name");
        referenceTypeBeanModel.add("edit", null);
        referenceTypeBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long referenceTypeId) {
        referenceType = referenceTypeHibernate.getById(referenceTypeId);
        referenceTypeHibernate.delete(referenceType);
    }
}
