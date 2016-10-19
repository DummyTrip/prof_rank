package com.diplomska.prof_rank.pages;


import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Start page of application prof_rank.
 */
@InstructorPage
public class Index
{
    @Inject
    ExcelWorkbook excelWorkbook;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Property
    Reference reference;

    @Inject
    RequestGlobals requestGlobals;

    @Inject
    Logger logger;

    @Inject
    ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @Inject
    RoleHibernate roleHibernate;

    @Inject
    RulebookHibernate rulebookHibernate;

    @Inject
    SectionHibernate sectionHibernate;

    public Float getPoints() {
        Float points = 0f;

        List<ReferenceInstance> referenceInstances = referenceInstanceHibernate.getAll();

        for (ReferenceInstance referenceInstance : referenceInstances) {
            Float referencePoints = referenceInstance.getReference().getPoints();
            if (referencePoints != null) {
                points += referenceInstance.getReference().getPoints();
            }
        }

        return points;
    }

    public List<Reference> getPopularReferences() {
        return referenceHibernate.getPopular(9);
    }

    public List<Reference> getPopularReferencesForPerson() {
        Person person = personHibernate.getById(Long.valueOf(1));
        if (person != null) {
            return referenceHibernate.getPopularByPerson(person, 9);
        } else {
            return new ArrayList<Reference>();
        }
    }

    public Section getSection() {
        return referenceHibernate.getSections(reference).get(0);
    }

    public Integer getNumberOfReferenceInstances() {
        List<ReferenceInstance> referenceInstancesOfCurrentReference = referenceInstanceHibernate.getByReference(reference);

        return referenceInstancesOfCurrentReference.size();
    }

    public Integer getNumberOfReferenceInstancesForPerson() {
        Person person = personHibernate.getById(Long.valueOf(1));

        List<ReferenceInstance> referenceInstancesOfCurrentReference = referenceInstanceHibernate.getByReferenceAndPerson(reference, person);

        return referenceInstancesOfCurrentReference.size();
    }

    public boolean isPopularReferencesNull() {
        return getPopularReferences().size() > 0 ? true : false;
    }

    public boolean isPopularReferencesForPersonNull() {
        return getPopularReferencesForPerson().size() > 0 ? true : false;
    }

    private void createDefaultReferenceInputTemplate() {
        ReferenceInputTemplate referenceInputTemplate = new ReferenceInputTemplate();
        referenceInputTemplate.setName("Default");
        Attribute attribute = new Attribute();
        attribute.setName("Title");
        attribute.setInputType("text");
        attributeHibernate.store(attribute);
        referenceInputTemplateHibernate.store(referenceInputTemplate);
        referenceInputTemplateHibernate.setAttribute(referenceInputTemplate, attribute);
    }

    private void addPerson() {
        Role role = new Role();
        role.setName("admin");
        roleHibernate.store(role);

        Person person = new Person();
        person.setFirstName("Vangel");
//        person.setMiddleName("");
        person.setLastName("Ajanovski");
        person.setEmail("vangel.ajanovski@finki.ukim.mk");
        person.setUserName("da");
        personHibernate.store(person);
        personHibernate.setRole(person, role);
    }

    private void addRulebookAndSections() {
        Rulebook rulebook = new Rulebook();
        rulebook.setName("Default");
        rulebookHibernate.store(rulebook);

        Section section = new Section();
        section.setName("Наставно-Образовна Дејност");
        sectionHibernate.store(section);
        rulebookHibernate.setSection(rulebook, section);

        section = new Section();
        section.setName("Наставно-Истражувачка Дејност");
        sectionHibernate.store(section);
        rulebookHibernate.setSection(rulebook, section);

        section = new Section();
        section.setName("Стручно-Апликативна Дејност");
        sectionHibernate.store(section);
        rulebookHibernate.setSection(rulebook, section);
    }

    private void addCategories(String fileName) throws Exception {
        Section section = sectionHibernate.getByColumn("name", "Наставно-Образовна Дејност").get(0);
        excelWorkbook.readCategorySpreadsheet(fileName, 1, section);
        section = sectionHibernate.getByColumn("name", "Наставно-Истражувачка Дејност").get(0);
        excelWorkbook.readCategorySpreadsheet(fileName, 2, section);
        section = sectionHibernate.getByColumn("name", "Стручно-Апликативна Дејност").get(0);
        excelWorkbook.readCategorySpreadsheet(fileName, 3, section);
    }

    private void addRefs(String fileName) throws Exception{
        Person person = personHibernate.getById(Long.valueOf(1));

        Section section = sectionHibernate.getByColumn("name", "Наставно-Образовна Дејност").get(0);
        excelWorkbook.readNastavaSpreadsheet(fileName, 4, person, section);

        section = sectionHibernate.getByColumn("name", "Наставно-Истражувачка Дејност").get(0);
        // read Projects sheet
        excelWorkbook.readSpreadsheet(fileName,
                5, "Projects", 2,
                "Име на проектот", "ПОЕНИ", person, section);

        section = sectionHibernate.getByColumn("name", "Наставно-Истражувачка Дејност").get(0);
        // read Papers sheet
        excelWorkbook.readSpreadsheet(fileName,
                6, "Papers", 2,
                "Автор 1", "//", person, section);

        section = sectionHibernate.getByColumn("name", "Наставно-Образовна Дејност").get(0);
        // read Books sheet
        excelWorkbook.readSpreadsheet(fileName,
                7, "Books", 1,
                "Автори", "ПОЕНИ", person, section);
    }

    @CommitAfter
    void onActionFromAddReferencesFromExcel() throws Exception{
        String fileName = "poi_test.xlsx";

        addCategories(fileName);
        addRefs(fileName);
    }

    @CommitAfter
    void onActionFromAddPersonAndReferenceInputTemplate() {
        addPerson();
        createDefaultReferenceInputTemplate();
        addRulebookAndSections();
    }
}
