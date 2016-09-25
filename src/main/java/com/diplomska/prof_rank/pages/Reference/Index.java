package com.diplomska.prof_rank.pages.Reference;

import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.ReferenceHibernate;
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
    private ReferenceHibernate referenceHibernate;

    @Property
    private Reference reference;

    @Property
    private BeanModel<Reference> referenceBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Reference> getReferences() {
        return referenceHibernate.getAll();
    }

    void setupRender() {
        referenceBeanModel = beanModelSource.createDisplayModel(Reference.class, messages);
        referenceBeanModel.include("name");
        referenceBeanModel.add("referenceType", pcs.create(Reference.class, "referenceType"));
        referenceBeanModel.add("show", null);
        referenceBeanModel.add("edit", null);
        referenceBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long referenceId) {
        reference = referenceHibernate.getById(referenceId);
        referenceHibernate.delete(reference);
    }
}
