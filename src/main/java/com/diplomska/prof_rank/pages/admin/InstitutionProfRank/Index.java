package com.diplomska.prof_rank.pages.admin.InstitutionProfRank;

import com.diplomska.prof_rank.entities.InstitutionProfRank;
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
    private InstitutionProfRank institutionProfRank;

    @Property
    private BeanModel<InstitutionProfRank> institutionBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<InstitutionProfRank> getInstitutionProfRanks() {
        return institutionHibernate.getAll();
    }

    void setupRender() {
        institutionBeanModel = beanModelSource.createDisplayModel(InstitutionProfRank.class, messages);
        institutionBeanModel.include("name", "city", "country");
        institutionBeanModel.add("show", null);
        institutionBeanModel.add("edit", null);
        institutionBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long institutionId) {
        institutionProfRank = institutionHibernate.getById(institutionId);
        institutionHibernate.delete(institutionProfRank);
    }
}
