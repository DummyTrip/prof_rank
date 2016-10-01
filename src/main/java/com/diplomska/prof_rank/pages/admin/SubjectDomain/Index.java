package com.diplomska.prof_rank.pages.admin.SubjectDomain;

import com.diplomska.prof_rank.entities.SubjectDomain;
import com.diplomska.prof_rank.services.SubjectDomainHibernate;
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
    private SubjectDomainHibernate subjectDomainHibernate;

    @Property
    private SubjectDomain subjectDomain;

    @Property
    private BeanModel<SubjectDomain> subjectDomainBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public List<SubjectDomain> getSubjectDomains() {
        return subjectDomainHibernate.getAll();
    }

    void setupRender() {
        subjectDomainBeanModel = beanModelSource.createDisplayModel(SubjectDomain.class, messages);
        subjectDomainBeanModel.include("name", "identifier");
        subjectDomainBeanModel.add("show", null);
        subjectDomainBeanModel.add("edit", null);
        subjectDomainBeanModel.add("delete", null);
    }

    @CommitAfter
    void onActionFromDelete(Long subjectDomainId) {
        subjectDomain = subjectDomainHibernate.getById(subjectDomainId);
        subjectDomainHibernate.delete(subjectDomain);
    }
}
