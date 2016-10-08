package com.diplomska.prof_rank.pages.admin.Report;

import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import java.util.List;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
@AdministratorPage
public class ShowReport {
    @Persist
    @Property
    private Long reportId;

    @Inject
    private ReportHibernate reportHibernate;

    @Inject
    private InstitutionHibernate institutionHibernate;

    @Inject
    private SubjectDomainHibernate subjectDomainHibernate;

    @Inject
    private ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    private UserHibernate userHibernate;

    @Persist
    @Property
    private Report report;

    @Property
    private User addUser;

    @Property
    private InstitutionProfRank institutionProfRank;

    @Property
    private InstitutionProfRank addInstitutionProfRank;

    @Property
    private SubjectDomain subjectDomain;

    @Property
    private SubjectDomain addSubjectDomain;

    @Property
    private ReferenceInstance referenceInstance;

    @Property
    private ReferenceInstance addReferenceInstance;

    @Property
    private BeanModel<User> addUserBeanModel;

    @Property
    private BeanModel<InstitutionProfRank> institutionBeanModel;

    @Property
    private BeanModel<InstitutionProfRank> addInstitutionBeanModel;

    @Property
    private BeanModel<SubjectDomain> subjectDomainBeanModel;

    @Property
    private BeanModel<SubjectDomain> addSubjectDomainBeanModel;

    @Property
    private BeanModel<ReferenceInstance> referenceInstanceBeanModel;

    @Property
    private BeanModel<ReferenceInstance> addReferenceInstanceBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public User getUser() {
        return reportHibernate.getUser(report);
    }

    public List<User> getAddUsers() {
        return userHibernate.getAll();
    }

    public List<InstitutionProfRank> getInstitutions() {
        return reportHibernate.getInstitutions(report);
    }

    public List<InstitutionProfRank> getAddInstitutions() {
        return institutionHibernate.getAll();
    }

    public List<SubjectDomain> getSubjectDomains() {
        return reportHibernate.getSubjectDomains(report);
    }

    public List<SubjectDomain> getAddSubjectDomains() {
        return subjectDomainHibernate.getAll();
    }

    public List<ReferenceInstance> getReferenceInstances() {
        return reportHibernate.getReferenceInstances(report);
    }

    public List<ReferenceInstance> getAddReferenceInstances() {
        return referenceInstanceHibernate.getAll();
    }

    void onActivate(Long reportId) {
        this.reportId = reportId;
    }

    Long passivate() {
        return reportId;
    }

    void setupRender() throws Exception {
        this.report = reportHibernate.getById(reportId);

        if (report == null) {
            throw new Exception("Report " + reportId + " does not exist.");
        }

        addUserBeanModel = beanModelSource.createDisplayModel(User.class, messages);
        addUserBeanModel.add("userFirstName", pcs.create(User.class, "firstName"));
        addUserBeanModel.add("userFatherName", pcs.create(User.class, "fatherName"));
        addUserBeanModel.add("userLastName", pcs.create(User.class, "lastName"));
        addUserBeanModel.add("userEmail", pcs.create(User.class, "email"));
        addUserBeanModel.add("add", null);

        institutionBeanModel = beanModelSource.createDisplayModel(InstitutionProfRank.class, messages);
        institutionBeanModel.add("institutionName", pcs.create(InstitutionProfRank.class, "name"));
        institutionBeanModel.add("institutionCountry", pcs.create(InstitutionProfRank.class, "country"));
        institutionBeanModel.add("institutionCity", pcs.create(InstitutionProfRank.class, "city"));
        institutionBeanModel.add("delete", null);

        addInstitutionBeanModel = beanModelSource.createDisplayModel(InstitutionProfRank.class, messages);
        addInstitutionBeanModel.add("subjectDomainName", pcs.create(InstitutionProfRank.class, "name"));
        addInstitutionBeanModel.add("institutionCountry", pcs.create(InstitutionProfRank.class, "country"));
        addInstitutionBeanModel.add("institutionCity", pcs.create(InstitutionProfRank.class, "city"));
        addInstitutionBeanModel.add("add", null);

        subjectDomainBeanModel = beanModelSource.createDisplayModel(SubjectDomain.class, messages);
        subjectDomainBeanModel.add("subjectDomainName", pcs.create(SubjectDomain.class, "name"));
        subjectDomainBeanModel.add("subjectDomainIdentifier", pcs.create(SubjectDomain.class, "identifier"));
        subjectDomainBeanModel.add("delete", null);

        addSubjectDomainBeanModel = beanModelSource.createDisplayModel(SubjectDomain.class, messages);
        addSubjectDomainBeanModel.add("institutionName", pcs.create(SubjectDomain.class, "name"));
        addSubjectDomainBeanModel.add("subjectDomainIdentifier", pcs.create(SubjectDomain.class, "identifier"));
        addSubjectDomainBeanModel.add("add", null);

        referenceInstanceBeanModel = beanModelSource.createDisplayModel(ReferenceInstance.class, messages);
        referenceInstanceBeanModel.add("referenceInstanceName", pcs.create(ReferenceInstance.class, "reference.name"));
        referenceInstanceBeanModel.add("delete", null);

        addReferenceInstanceBeanModel = beanModelSource.createDisplayModel(ReferenceInstance.class, messages);
        addReferenceInstanceBeanModel.add("referenceInstanceName", pcs.create(ReferenceInstance.class, "reference.name"));
        addReferenceInstanceBeanModel.add("add", null);
    }

    public boolean isUserNull() {
        return report.getUser() == null ? false : true;
//        return false;
    }

    @CommitAfter
    void onActionFromAddUser(Long id) {
        User entity = userHibernate.getById(id);

        reportHibernate.setUser(report, entity);
    }

    @CommitAfter
    void onActionFromAddInstitution(Long id) {
        InstitutionProfRank entity = institutionHibernate.getById(id);

        reportHibernate.setInstitution(report, entity);
    }

    @CommitAfter
    void onActionFromDeleteInstitution(Long id) {
        InstitutionProfRank entity = institutionHibernate.getById(id);

        reportHibernate.deleteInstitution(report, entity);
    }

    @CommitAfter
    void onActionFromDeleteSubjectDomain(Long id) {
        SubjectDomain entity = subjectDomainHibernate.getById(id);

        reportHibernate.deleteSubjectDomain(report, entity);
    }

    @CommitAfter
    void onActionFromAddSubjectDomain(Long id) {
        SubjectDomain entity = subjectDomainHibernate.getById(id);

        reportHibernate.setSubjectDomain(report, entity);
    }

    @CommitAfter
    void onActionFromDeleteReferenceInstance(Long id) {
        ReferenceInstance entity = referenceInstanceHibernate.getById(id);

        reportHibernate.deleteReferenceInstance(report, entity);
    }

    @CommitAfter
    void onActionFromAddReferenceInstance(Long id) {
        ReferenceInstance entity = referenceInstanceHibernate.getById(id);

        reportHibernate.setReferenceInstance(report, entity);
    }
}
