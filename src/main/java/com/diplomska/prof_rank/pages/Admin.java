package com.diplomska.prof_rank.pages;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Created by Aleksandar on 26-Nov-16.
 */
public class Admin {
    @Inject
    private AttributeHibernate attributeHibernate;

    @Inject
    private ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @Inject
    private RoleHibernate roleHibernate;

    @Inject
    private PersonHibernate personHibernate;

    @Inject
    private RulebookHibernate rulebookHibernate;

    @Inject
    private SectionHibernate sectionHibernate;

    @Inject
    private ExcelWorkbook excelWorkbook;



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
