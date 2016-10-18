package com.diplomska.prof_rank.pages;


import com.diplomska.prof_rank.annotations.AdministratorPage;
import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.annotations.PublicPage;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.entities.Section;
import com.diplomska.prof_rank.entities.User;
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
    UserHibernate userHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Property
    Reference reference;

    @Inject
    RequestGlobals requestGlobals;

    @Inject
    Logger logger;

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

    public List<Reference> getPopularReferencesForUser() {
        User user = userHibernate.getById(Long.valueOf(1));
        if (user != null) {
            return referenceHibernate.getPopularByUser(user, 9);
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

    public Integer getNumberOfReferenceInstancesForUser() {
        User user = userHibernate.getById(Long.valueOf(1));
        List<ReferenceInstance> referenceInstancesOfCurrentReference = referenceInstanceHibernate.getByReferenceAndUser(reference, user);
//        List<ReferenceInstance> referenceInstancesOfCurrentReference = referenceInstanceHibernate.getByReference(reference);

        return referenceInstancesOfCurrentReference.size();
    }

    public boolean isPopularReferencesNull() {
        return getPopularReferences().size() > 0 ? true : false;
    }

    public boolean isPopularReferencesForUserNull() {
        return getPopularReferencesForUser().size() > 0 ? true : false;
    }

    @CommitAfter
    void onActionFromAddReferencesFromExcel() throws Exception{
        User user = new User();
        user.setFirstName("TestFirstName");
        user.setFatherName("TestFatherName");
        user.setLastName("TestLastName");
        user.setEmail("TestEmail");
        userHibernate.store(user);

        Person person = new Person();
        person.setFirstName("Vangel");
//        person.setMiddleName("");
        person.setLastName("Ajanovski");
        person.setEmail("vangel.ajanovski@finki.ukim.mk");
        personHibernate.store(person);

        String fileName = "poi_test.xlsx";

        excelWorkbook.readCategorySpreadsheet(fileName, 1);
        excelWorkbook.readCategorySpreadsheet(fileName, 2);
        excelWorkbook.readCategorySpreadsheet(fileName, 3);

        excelWorkbook.readNastavaSpreadsheet(fileName, 4, person);
        
//        // read Projects sheet
//        excelWorkbook.readSpreadsheet(fileName,
//                5, "Projects", 2,
//                "Име на проектот", "ПОЕНИ");
//
//        // read Papers sheet
//        excelWorkbook.readSpreadsheet(fileName,
//                6, "Papers", 2,
//                "Автор 1", "//");
//
//        // read Books sheet
//        excelWorkbook.readSpreadsheet(fileName,
//                7, "Books", 1,
//                "Автори", "ПОЕНИ");
    }
}
