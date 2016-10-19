package com.diplomska.prof_rank.pages.admin.Reference;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.AttributeReferenceInstance;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 28-Sep-16.
 */
@AdministratorPage
public class ShowReference {
    @Property
    @Persist
    private Long referenceId;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Property
    Reference reference;

    @Property
    private AttributeReferenceInstance attributeReferenceInstance;

    @Property
    private BeanModel<AttributeReferenceInstance> attributeReferenceInstanceBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<AttributeReferenceInstance> getAttributeReferenceInstances() {
        return reference.getAttributeReferenceInstances();
    }

    void onActivate(Long referenceId) {
        this.referenceId = referenceId;
    }

    Long passivate() {
        return referenceId;
    }

    void setupRender() throws Exception {
        this.reference = referenceHibernate.getById(referenceId);

        if (reference == null) {
            throw new Exception("ReferenceType " + referenceId + " does not exist.");
        }

        attributeReferenceInstanceBeanModel = beanModelSource.createDisplayModel(AttributeReferenceInstance.class, messages);
        attributeReferenceInstanceBeanModel.include();
        attributeReferenceInstanceBeanModel.add("attributeReferenceInstanceName", pcs.create(AttributeReferenceInstance.class, "attribute.name"));
        attributeReferenceInstanceBeanModel.add("attributeReferenceInstanceInputType", pcs.create(AttributeReferenceInstance.class, "attribute.inputType"));
        attributeReferenceInstanceBeanModel.add("attributeReferenceInstanceVal", pcs.create(AttributeReferenceInstance.class, "value"));
        attributeReferenceInstanceBeanModel.add("delete", null);
    }



    }
