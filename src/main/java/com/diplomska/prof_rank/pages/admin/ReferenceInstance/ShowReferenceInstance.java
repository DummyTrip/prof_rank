package com.diplomska.prof_rank.pages.admin.ReferenceInstance;

import com.diplomska.prof_rank.entities.AttributeReferenceInstance;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
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
public class ShowReferenceInstance {
    @Property
    @Persist
    private Long referenceInstanceId;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Property
    ReferenceInstance referenceInstance;

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
        return referenceInstance.getAttributeReferenceInstances();
    }

    void onActivate(Long referenceInstanceId) {
        this.referenceInstanceId = referenceInstanceId;
    }

    Long passivate() {
        return referenceInstanceId;
    }

    void setupRender() throws Exception {
        this.referenceInstance = referenceInstanceHibernate.getById(referenceInstanceId);

        if (referenceInstance == null) {
            throw new Exception("Reference " + referenceInstanceId + " does not exist.");
        }

        attributeReferenceInstanceBeanModel = beanModelSource.createDisplayModel(AttributeReferenceInstance.class, messages);
        attributeReferenceInstanceBeanModel.include();
        attributeReferenceInstanceBeanModel.add("attributeReferenceInstanceName", pcs.create(AttributeReferenceInstance.class, "attribute.name"));
        attributeReferenceInstanceBeanModel.add("attributeReferenceInstanceInputType", pcs.create(AttributeReferenceInstance.class, "attribute.inputType"));
        attributeReferenceInstanceBeanModel.add("attributeReferenceInstanceVal", pcs.create(AttributeReferenceInstance.class, "value"));
        attributeReferenceInstanceBeanModel.add("delete", null);
    }



    }
