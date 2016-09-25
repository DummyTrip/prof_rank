package com.diplomska.prof_rank.pages.Report;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PropertyConduitSource;

import javax.jws.soap.SOAPBinding;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 25-Sep-16.
 */
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
    private ReferenceHibernate referenceHibernate;

    @Inject
    private UserHibernate userHibernate;

    @Persist
    @Property
    private Report report;

    @Property
    private User addUser;

    @Property
    private Institution institution;

    @Property
    private Institution addInstitution;

    @Property
    private SubjectDomain subjectDomain;

    @Property
    private SubjectDomain addSubjectDomain;

    @Property
    private Reference reference;

    @Property
    private Reference addReference;

    @Property
    private BeanModel<User> addUserBeanModel;

    @Property
    private BeanModel<Institution> institutionBeanModel;

    @Property
    private BeanModel<Institution> addInstitutionBeanModel;

    @Property
    private BeanModel<SubjectDomain> subjectDomainBeanModel;

    @Property
    private BeanModel<SubjectDomain> addSubjectDomainBeanModel;

    @Property
    private BeanModel<Reference> referenceBeanModel;

    @Property
    private BeanModel<Reference> addReferenceBeanModel;

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

    public List<Institution> getInstitutions() {
        return reportHibernate.getInstitutions(report);
    }

    public List<Institution> getAddInstitutions() {
        return institutionHibernate.getAll();
    }

    public List<SubjectDomain> getSubjectDomains() {
        return reportHibernate.getSubjectDomains(report);
    }

    public List<SubjectDomain> getAddSubjectDomains() {
        return subjectDomainHibernate.getAll();
    }

    public List<Reference> getReferences() {
        return reportHibernate.getReferences(report);
    }

    public List<Reference> getAddReferences() {
        return referenceHibernate.getAll();
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

        institutionBeanModel = beanModelSource.createDisplayModel(Institution.class, messages);
        institutionBeanModel.add("institutionName", pcs.create(Institution.class, "name"));
        institutionBeanModel.add("institutionCountry", pcs.create(Institution.class, "country"));
        institutionBeanModel.add("institutionCity", pcs.create(Institution.class, "city"));
        institutionBeanModel.add("delete", null);

        addInstitutionBeanModel = beanModelSource.createDisplayModel(Institution.class, messages);
        addInstitutionBeanModel.add("subjectDomainName", pcs.create(Institution.class, "name"));
        addInstitutionBeanModel.add("institutionCountry", pcs.create(Institution.class, "country"));
        addInstitutionBeanModel.add("institutionCity", pcs.create(Institution.class, "city"));
        addInstitutionBeanModel.add("add", null);

        subjectDomainBeanModel = beanModelSource.createDisplayModel(SubjectDomain.class, messages);
        subjectDomainBeanModel.add("subjectDomainName", pcs.create(SubjectDomain.class, "name"));
        subjectDomainBeanModel.add("subjectDomainIdentifier", pcs.create(SubjectDomain.class, "identifier"));
        subjectDomainBeanModel.add("delete", null);

        addSubjectDomainBeanModel = beanModelSource.createDisplayModel(SubjectDomain.class, messages);
        addSubjectDomainBeanModel.add("institutionName", pcs.create(SubjectDomain.class, "name"));
        addSubjectDomainBeanModel.add("subjectDomainIdentifier", pcs.create(SubjectDomain.class, "identifier"));
        addSubjectDomainBeanModel.add("add", null);

        referenceBeanModel = beanModelSource.createDisplayModel(Reference.class, messages);
        referenceBeanModel.add("referenceName", pcs.create(Reference.class, "name"));
        referenceBeanModel.add("delete", null);

        addReferenceBeanModel = beanModelSource.createDisplayModel(Reference.class, messages);
        addReferenceBeanModel.add("referenceName", pcs.create(Reference.class, "name"));
        addReferenceBeanModel.add("add", null);
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
        Institution entity = institutionHibernate.getById(id);

        reportHibernate.setInstitution(report, entity);
    }

    @CommitAfter
    void onActionFromDeleteInstitution(Long id) {
        Institution entity = institutionHibernate.getById(id);

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
    void onActionFromDeleteReference(Long id) {
        Reference entity = referenceHibernate.getById(id);

        reportHibernate.deleteReference(report, entity);
    }

    @CommitAfter
    void onActionFromAddReference(Long id) {
        Reference entity = referenceHibernate.getById(id);

        reportHibernate.setReference(report, entity);
    }
}
