package com.diplomska.prof_rank.pages.Report;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 05-Nov-16.
 */
public class ShowReport {
    @Persist
    @Property
    private
    Long reportId;

    @Property
    private Person person;

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

    public void setupRender() {
        // TODO: only commission and this report's owner can see this page.

        report = reportHibernate.getById(reportId);
        person = reportHibernate.getPerson(report);

        // TODO: change to the most recent rulebook. Or add another logic for rulebooks and reports.
        rulebook = rulebookHibernate.getById(Long.valueOf(1));

        references = reportHibernate.getReferences(report);
        loadReferenceTypes();
    }

    private void loadReferenceTypes() {
        referenceTypes = new ArrayList<ReferenceType>();

        for (Reference reference : references) {
            ReferenceType referenceType = reference.getReferenceType();
            if (!referenceTypes.contains(referenceType)) {
                referenceTypes.add(referenceType);
            }
        }
    }

    public List<Section> getSections() {
        return rulebookHibernate.getSections(rulebook);
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
        // TODO: concatenate institution names.
        return "";
    }

    public String getSubjectDomainNames() {
        // TODO: concatenate subject domain names.
        return "";
    }

    public String getCommissionerName() {
        StringBuilder sb = buildPersonName(commissioner);

        return sb.toString();
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
}
