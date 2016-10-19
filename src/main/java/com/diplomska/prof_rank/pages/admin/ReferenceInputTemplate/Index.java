package com.diplomska.prof_rank.pages.admin.ReferenceInputTemplate;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.ReferenceInputTemplate;
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
@AdministratorPage
public class Index {
    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    private AttributeHibernate attributeHibernate;

    @Property
    private ReferenceInputTemplate referenceInputTemplate;

    @Property
    private Attribute attribute;

    @Property
    private BeanModel<ReferenceInputTemplate> referenceInputTemplateBeanModel;

    @Property
    private BeanModel<Attribute> attributeBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<ReferenceInputTemplate> getReferenceInputTemplates() {
        return referenceTypeHibernate.getAll();
    }

    public List<Attribute> getAttributes() {
        return attributeHibernate.getAll();
    }

    void setupRender() {
        referenceInputTemplateBeanModel = beanModelSource.createDisplayModel(ReferenceInputTemplate.class, messages);
        referenceInputTemplateBeanModel.include("name");
        referenceInputTemplateBeanModel.add("show", null);
        referenceInputTemplateBeanModel.add("edit", null);
        referenceInputTemplateBeanModel.add("delete", null);

        attributeBeanModel = beanModelSource.createDisplayModel(Attribute.class, messages);
        attributeBeanModel.include("name", "inputType");
        attributeBeanModel.add("add", null);
    }

    @CommitAfter
    void onActionFromDelete(Long referenceTypeId) {
        referenceInputTemplate = referenceTypeHibernate.getById(referenceTypeId);
        referenceTypeHibernate.delete(referenceInputTemplate);
    }
}
