package com.diplomska.prof_rank.pages;


import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.annotations.PublicPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.model.UserInfo;
import com.diplomska.prof_rank.services.*;
import com.diplomska.prof_rank.util.AppConfig;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Start page of application prof_rank.
 */
@PublicPage
public class Index
{
    @Inject
    ExcelWorkbook excelWorkbook;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    UserHibernate userHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Property
    Reference reference;

    @Inject
    RequestGlobals requestGlobals;

    @Inject
    Logger logger;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @Inject
    RoleHibernate roleHibernate;

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

    private void createDefaultReferenceType() {
        ReferenceType referenceType = new ReferenceType();
        referenceType.setName("Default");
        Attribute attribute = new Attribute();
        attribute.setName("Title");
        attribute.setInputType("text");
        attributeHibernate.store(attribute);
        referenceTypeHibernate.store(referenceType);
        referenceTypeHibernate.setAttribute(referenceType, attribute);
    }

    private void addUser() {
        User user = new User();
        user.setFirstName("TestFirstName");
        user.setFatherName("TestFatherName");
        user.setLastName("TestLastName");
        user.setEmail("TestEmail");
        userHibernate.store(user);
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

    private void addCategories(String fileName) throws Exception {
        excelWorkbook.readCategorySpreadsheet(fileName, 1);
        excelWorkbook.readCategorySpreadsheet(fileName, 2);
        excelWorkbook.readCategorySpreadsheet(fileName, 3);
    }

    private void addRefs(String fileName) throws Exception{
        Person person = personHibernate.getById(Long.valueOf(1));

        excelWorkbook.readNastavaSpreadsheet(fileName, 4, person);

        // read Projects sheet
        excelWorkbook.readSpreadsheet(fileName,
                5, "Projects", 2,
                "Име на проектот", "ПОЕНИ", person);

        // read Papers sheet
        excelWorkbook.readSpreadsheet(fileName,
                6, "Papers", 2,
                "Автор 1", "//", person);

        // read Books sheet
        excelWorkbook.readSpreadsheet(fileName,
                7, "Books", 1,
                "Автори", "ПОЕНИ", person);
    }

    @CommitAfter
    void onActionFromAddReferencesFromExcel() throws Exception{
        String fileName = "poi_test.xlsx";

        addCategories(fileName);
        addRefs(fileName);
    }

    @CommitAfter
    void onActionFromAddPersonAndReferenceType() {
        addUser();
        addPerson();
        createDefaultReferenceType();
    }
}
