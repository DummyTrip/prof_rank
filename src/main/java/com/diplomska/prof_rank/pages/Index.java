package com.diplomska.prof_rank.pages;


import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.commons.beanutils.converters.LongConverter;
import org.apache.commons.codec.language.bm.Rule;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Start page of application prof_rank.
 */
@InstructorPage
public class Index
{
    @Inject
    ExcelWorkbook excelWorkbook;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Property
    ReferenceType referenceType;

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

        List<Reference> references = referenceHibernate.getAll();

        for (Reference reference : references) {
            Float referencePoints = reference.getReferenceType().getPoints();
            if (referencePoints != null) {
                points += reference.getReferenceType().getPoints();
            }
        }

        return points;
    }

    public List<ReferenceType> getPopularReferenceTypes() {
        return referenceTypeHibernate.getPopular(9);
    }

    public List<ReferenceType> getPopularReferenceTypesForPerson() {
        Person person = personHibernate.getById(Long.valueOf(1));
        if (person != null) {
            return referenceTypeHibernate.getPopularByPerson(person, 9);
        } else {
            return new ArrayList<ReferenceType>();
        }
    }

    public Section getSection() {
        return referenceTypeHibernate.getSections(referenceType).get(0);
    }

    public Integer getNumberOfReferences() {
        List<Reference> referencesOfCurrentReferenceType = referenceHibernate.getByReferenceType(referenceType);

        return referencesOfCurrentReferenceType.size();
    }

    public Integer getNumberOfReferencesForPerson() {
        Person person = personHibernate.getById(Long.valueOf(1));

        List<Reference> referencesOfCurrentReferenceType = referenceHibernate.getByReferenceTypeAndPerson(referenceType, person);

        return referencesOfCurrentReferenceType.size();
    }

    public boolean isPopularReferenceTypesNull() {
        return getPopularReferenceTypes().size() > 0 ? true : false;
    }

    public boolean isPopularReferenceTypesForPersonNull() {
        return getPopularReferenceTypesForPerson().size() > 0 ? true : false;
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

    private void addCategories(String fileName, Rulebook rulebook, ReferenceInputTemplate referenceInputTemplate) throws Exception {
        Section section = sectionHibernate.getByColumn("name", "Наставно-Образовна Дејност").get(0);
        excelWorkbook.readCategorySpreadsheet(fileName, 1, rulebook, section, referenceInputTemplate);
        section = sectionHibernate.getByColumn("name", "Наставно-Истражувачка Дејност").get(0);
        excelWorkbook.readCategorySpreadsheet(fileName, 2, rulebook, section, referenceInputTemplate);
        section = sectionHibernate.getByColumn("name", "Стручно-Апликативна Дејност").get(0);
        excelWorkbook.readCategorySpreadsheet(fileName, 3, rulebook, section, referenceInputTemplate);
    }

    private void addRefs(String fileName) throws Exception{
        Person person = personHibernate.getById(Long.valueOf(1));
        Rulebook rulebook = rulebookHibernate.getById(Long.valueOf(1));

        Section section = sectionHibernate.getByColumn("name", "Наставно-Образовна Дејност").get(0);
        excelWorkbook.readNastavaSpreadsheet(fileName, 4, person, rulebook, section);

        section = sectionHibernate.getByColumn("name", "Наставно-Истражувачка Дејност").get(0);
        // read Projects sheet
        excelWorkbook.readSpreadsheet(fileName,
                5, "Projects", 2,
                "Име на проектот", "ПОЕНИ", person, rulebook, section);

        section = sectionHibernate.getByColumn("name", "Наставно-Истражувачка Дејност").get(0);
        // read Papers sheet
        excelWorkbook.readSpreadsheet(fileName,
                6, "Papers", 2,
                "Автор 1", "//", person, rulebook, section);

        section = sectionHibernate.getByColumn("name", "Наставно-Образовна Дејност").get(0);
        // read Books sheet
        excelWorkbook.readSpreadsheet(fileName,
                7, "Books", 1,
                "Автори", "ПОЕНИ", person, rulebook, section);
    }

    @CommitAfter
    void onActionFromAddPersonAndReferenceInputTemplate(){
        addPerson();
        createDefaultReferenceInputTemplate();
        addRulebookAndSections();
    }

    @CommitAfter
    void onActionFromAddReferencesFromExcel() throws Exception{
        String fileName = "poi_test.xlsx";
        Rulebook rulebook = rulebookHibernate.getById(Long.valueOf(1));
        ReferenceInputTemplate referenceInputTemplate = referenceInputTemplateHibernate.getById(Long.valueOf(1));

        addCategories(fileName, rulebook, referenceInputTemplate);
        addRefs(fileName);
    }

}
