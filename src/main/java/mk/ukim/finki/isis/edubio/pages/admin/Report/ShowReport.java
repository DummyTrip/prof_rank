package mk.ukim.finki.isis.edubio.pages.admin.Report;

import mk.ukim.finki.isis.edubio.annotations.AdministratorPage;
import mk.ukim.finki.isis.edubio.entities.InstitutionProfRank;
import mk.ukim.finki.isis.edubio.entities.Reference;
import mk.ukim.finki.isis.edubio.entities.Report;
import mk.ukim.finki.isis.edubio.entities.SubjectDomain;
import mk.ukim.finki.isis.edubio.services.*;
import mk.ukim.finki.isis.model.entities.Person;
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
    private ReferenceHibernate referenceHibernate;

    @Inject
    private PersonHibernate personHibernate;

    @Persist
    @Property
    private Report report;

    @Property
    private Person addPerson;

    @Property
    private InstitutionProfRank institutionProfRank;

    @Property
    private InstitutionProfRank addInstitutionProfRank;

    @Property
    private SubjectDomain subjectDomain;

    @Property
    private SubjectDomain addSubjectDomain;

    @Property
    private Reference reference;

    @Property
    private Reference addReference;

    @Property
    private BeanModel<Person> addPersonBeanModel;

    @Property
    private BeanModel<InstitutionProfRank> institutionBeanModel;

    @Property
    private BeanModel<InstitutionProfRank> addInstitutionBeanModel;

    @Property
    private BeanModel<SubjectDomain> subjectDomainBeanModel;

    @Property
    private BeanModel<SubjectDomain> addSubjectDomainBeanModel;

    @Property
    private BeanModel<Reference> referenceBeanModel;

    @Property
    private BeanModel<Reference> addreferenceBeanModel;

    @Inject
    private BeanModelSource beanModelSource;

    @Inject
    private Messages messages;

    @Inject
    private PropertyConduitSource pcs;

    public Person getPerson() {
        return reportHibernate.getPerson(report);
    }

    public List<Person> getAddPersons() {
        return personHibernate.getAll();
    }

    public List<InstitutionProfRank> getInstitutionProfRanks() {
        return reportHibernate.getInstitutions(report);
    }

    public List<InstitutionProfRank> getAddInstitutionProfRanks() {
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

        addPersonBeanModel = beanModelSource.createDisplayModel(Person.class, messages);
        addPersonBeanModel.add("add", null);

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

        referenceBeanModel = beanModelSource.createDisplayModel(Reference.class, messages);
        referenceBeanModel.add("referenceName", pcs.create(Reference.class, "reference.name"));
        referenceBeanModel.add("delete", null);

        addreferenceBeanModel = beanModelSource.createDisplayModel(Reference.class, messages);
        addreferenceBeanModel.add("referenceName", pcs.create(Reference.class, "reference.name"));
        addreferenceBeanModel.add("add", null);
    }

    public boolean isPersonNull() {
        return report.getPerson() == null ? false : true;
//        return false;
    }

    @CommitAfter
    void onActionFromAddPerson(Long id) {
        Person entity = personHibernate.getById(id);

        reportHibernate.setPerson(report, entity);
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
