package com.diplomska.prof_rank.pages.Report;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.model.UserInfo;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Institution;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.annotations.*;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

import java.util.*;

/**
 * Created by Aleksandar on 05-Nov-16.
 */
public class ShowReport {
    @Persist
    @Property
    private
    Long reportId;

    @ActivationRequestParameter(value = "edit")
    private boolean editReport;

    @Persist
    @Property
    private Person person;

    @Persist
    @Property
    private Report report;

    @Inject
    private ReportHibernate reportHibernate;

    @Inject
    private PersonHibernate personHibernate;

    @Inject
    private ReferenceHibernate referenceHibernate;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    private RulebookHibernate rulebookHibernate;

    @Inject
    private SectionHibernate sectionHibernate;

    @Property
    private Section section;

    @Property
    private List<Reference> references;

    @Property
    private List<ReferenceType> referenceTypes;

    @Property
    private Rulebook rulebook;

    @Property
    private Reference reference;

    @Property
    private ReferenceType referenceType;

    @Property
    private Integer loopIndex;

    @Property
    private Person commissioner;

    @Property
    private Integer commissionIndex;
    
    @Property
    private Map<ReferenceType, Float> referenceTypePointsMap;

    @Property
    private Map<Section, Float> totalSectionPointsMap;

    @Persist
    @Property
    private String newInstitutionName;

    @Persist
    @Property
    private String newSubjectDomainName;

    @Inject
    private InstitutionHibernate institutionHibernate;

    @Inject
    private SubjectDomainHibernate subjectDomainHibernate;

    @Inject
    private AjaxResponseRenderer ajaxResponseRenderer;

    @InjectComponent
    private Zone reportInfoZone, referencesZone;

    @Property
    private InstitutionProfRank institution;

    @Property
    private SubjectDomain subjectDomain;

    @SessionState
    private UserInfo userInfo;

    @Property
    private boolean showPage;

    public void setupRender() {
        report = reportHibernate.getById(reportId);
        person = reportHibernate.getPerson(report);

        authenticatePage();

        // TODO: change to the most recent rulebook. Or add another logic for rulebooks and reports.
        rulebook = rulebookHibernate.getById(Long.valueOf(1));

        references = reportHibernate.getReferences(report);
        loadReferenceTypes();
    }

    private void authenticatePage() {
        List<Person> persons = personHibernate.getCommission(person);
        persons.add(person);
        for (Person person : persons) {
            if (person.getPersonId() == userInfo.getAdminId()){
                showPage = true;
            }
        }
    }

    private void loadReferenceTypes() {
        referenceTypes = new ArrayList<ReferenceType>();
        referenceTypePointsMap = new HashMap<ReferenceType, Float>();

        for (Reference reference : references) {
            ReferenceType referenceType = reference.getReferenceType();
            if (!referenceTypes.contains(referenceType)) {
                referenceTypes.add(referenceType);
                referenceTypePointsMap.put(referenceType, 0f);
            }
            if (referenceType.getPoints() != null) {
                referenceTypePointsMap.put(referenceType, referenceTypePointsMap.get(referenceType) + referenceType.getPoints());
            }
        }
    }

    public List<InstitutionProfRank> getInstitutions() {
        return reportHibernate.getInstitutions(report);
    }

    public List<SubjectDomain> getSubjectDomains() {
        return reportHibernate.getSubjectDomains(report);
    }

    public List<Reference> getReferencesByReferenceType() {
        return referenceHibernate.getByReferenceTypeAndPerson(referenceType, person);
    }

    public List<Section> getSections() {
        List<Section> sections = rulebookHibernate.getSections(rulebook);

        if (totalSectionPointsMap == null) {
            totalSectionPointsMap = new HashMap<Section, Float>();
            for (Section section : sections) {
                totalSectionPointsMap.put(section, 0f);
            }
        }

        return sections;
    }

    public List<ReferenceType> getReferenceTypesBySection() {
        List<ReferenceType> referenceTypesBySection = new ArrayList<ReferenceType>();

        for (ReferenceType referenceType: referenceTypes) {
            List<Section> sections = referenceTypeHibernate.getSections(referenceType);
            for (Section referenceSection : sections) {
                if (referenceSection.getId().equals(section.getId())) {
                    referenceTypesBySection.add(referenceType);
                    break;
                }
            }
        }

        return referenceTypesBySection;
    }

    public boolean getEditReport() {
        return editReport;
    }

    public List<Person> getCommission() {
        return personHibernate.getCommission(person);
    }

    public String getSectionName() {
        return section.getName().toUpperCase();
    }

    public String getPersonName() {
        StringBuilder sb = buildPersonName(person);

        return sb.toString().toUpperCase();
    }

    public String getInstitutionNames() {
        List<InstitutionProfRank> institutions = reportHibernate.getInstitutions(report);
        List<String> institutionNames = new ArrayList<String>();
        for (InstitutionProfRank institutionProfRank : institutions) {
            institutionNames.add(institutionProfRank.getName());
        }

        return StringUtils.join(institutionNames, ", ");
    }

    public String getSubjectDomainNames() {
        List<SubjectDomain> subjectDomains = reportHibernate.getSubjectDomains(report);
        List<String> subjectDomainNames = new ArrayList<String>();
        for (SubjectDomain subjectDomain : subjectDomains) {
            subjectDomainNames.add(subjectDomain.getName());
        }

        return StringUtils.join(subjectDomainNames, ", ");
    }

    public String getCommissionerName() {
        StringBuilder sb = buildPersonName(commissioner);

        return sb.toString();
    }

    public Float getReferenceTypePoints() {
        sumSectionPoints();

        return referenceTypePointsMap.get(referenceType);
    }

    private void sumSectionPoints() {
        totalSectionPointsMap.put(section, totalSectionPointsMap.get(section) + referenceTypePointsMap.get(referenceType));
    }

    public Float getSectionPoints() {
        return totalSectionPointsMap.get(section);
    }

    public Float getTotalPoints() {
        return report.getTotalScore();
    }

    private StringBuilder buildPersonName(Person person) {
        StringBuilder sb = new StringBuilder();

        sb.append(person.getFirstName());
        sb.append(" ");
        if (person.getMiddleName() != null) {
            sb.append(person.getMiddleName());
            sb.append(" ");
        }
        sb.append(person.getLastName());

        return sb;
    }

    public String getReferenceDisplayName() {
        return referenceHibernate.getDisplayName(reference);
    }

    public String getActiveReferenceClass() {
        return getActiveReference().equals("-") ? "btn-primary" : "btn-default";
    }

    public String getActiveReference() {
        if (references.contains(reference)) {
            return "-";
        } else {
            return "+";
        }
    }

    public Integer getReferenceTypeIndex() {
        return loopIndex + 1;
    }

    public Integer getCommissionerIndex() {
        return commissionIndex + 1;
    }

    public void onActivate(Long reportId) {
        this.reportId = reportId;
    }

    public Long onPassivate() {
        return reportId;
    }

    List<String> onProvideCompletionsFromNewInstitutionField(String partial) {
        List<String> matches = new ArrayList<String>();
        partial = partial.toUpperCase();

        for (String institutionName : institutionHibernate.findAllInstitutionNames()) {
            if (institutionName.toUpperCase().startsWith(partial)) {
                matches.add(institutionName);
            }
        }

        return matches;
    }

    List<String> onProvideCompletionsFromNewSubjectDomainField(String partial) {
        List<String> matches = new ArrayList<String>();
        partial = partial.toUpperCase();

        for (String subjectDomainName : subjectDomainHibernate.findAllSubjectDomainNames()) {
            if (subjectDomainName.toUpperCase().startsWith(partial)) {
                matches.add(subjectDomainName);
            }
        }

        return matches;
    }

    @CommitAfter
    @OnEvent(component = "saveNewInstitution", value = "selected")
    public void saveNewInstitution() {
        InstitutionProfRank institutionProfRank = institutionHibernate.getByColumn("name", newInstitutionName).get(0);
        report = reportHibernate.getById(reportId);
        reportHibernate.setInstitution(report, institutionProfRank);

        ajaxResponseRenderer.addRender(reportInfoZone);
    }

    @CommitAfter
    @OnEvent(component = "removeInstitution", value = "selected")
    public void removeInstitution(InstitutionProfRank institution) {
        Report report = reportHibernate.getById(reportId);
        reportHibernate.deleteInstitution(report, institution);

        ajaxResponseRenderer.addRender(reportInfoZone);
    }

    @CommitAfter
    @OnEvent(component = "saveNewSubjectDomain", value = "selected")
    public void saveNewSubjectDomain() {
        report = reportHibernate.getById(reportId);
        reportHibernate.setSubjectDomain(report, subjectDomainHibernate.getByColumn("name", newSubjectDomainName).get(0));

        ajaxResponseRenderer.addRender(reportInfoZone);
    }

    @CommitAfter
    @OnEvent(component = "removeSubjectDomain", value = "selected")
    public void removeSubjectDomain(SubjectDomain subjectDomain) {
        Report report = reportHibernate.getById(reportId);
        reportHibernate.deleteSubjectDomain(report, subjectDomain);

        ajaxResponseRenderer.addRender(reportInfoZone);
    }

    @CommitAfter
    @OnEvent(component = "toggleActiveReference", value = "selected")
    public void toggleActiveReference(Reference reference) {
        Report report = reportHibernate.getById(reportId);
        if (reportHibernate.getReferences(report).contains(reference)) {
            reportHibernate.deleteReference(report, reference);
        } else {
            reportHibernate.setReference(report, reference);
        }

        ajaxResponseRenderer.addRender(referencesZone);
    }
}
