package com.diplomska.prof_rank.pages.admin.Institution;

import com.diplomska.prof_rank.entities.Institution;
import com.diplomska.prof_rank.services.InstitutionHibernate;
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
    private InstitutionHibernate institutionHibernate;

    @Property
    private Institution institution;

    @Property
    private BeanModel<Institution> institutionBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<Institution> getInstitutions() {
        return institutionHibernate.getAll();
    }

    void setupRender() {
        institutionBeanModel = beanModelSource.createDisplayModel(Institution.class, messages);
        institutionBeanModel.include("name", "city", "country");
        institutionBeanModel.add("show", null);
        institutionBeanModel.add("edit", null);
        institutionBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long institutionId) {
        institution = institutionHibernate.getById(institutionId);
        institutionHibernate.delete(institution);
    }
}
