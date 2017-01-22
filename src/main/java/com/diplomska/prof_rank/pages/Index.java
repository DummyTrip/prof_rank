package com.diplomska.prof_rank.pages;


import com.diplomska.prof_rank.annotations.InstructorPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.model.UserInfo;
import com.diplomska.prof_rank.services.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Start page of application prof_rank.
 */
@InstructorPage
public class Index
{
    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Property
    ReferenceType referenceType;

    @SessionState
    private UserInfo userInfo;

    public Float getPoints() {
        Float points = 0f;

        List<Reference> references = referenceHibernate.getAll();

        // calculate points
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
        if (userInfo != null && userInfo.getPerson() != null) {
            return referenceTypeHibernate.getPopularByPerson(userInfo.getPerson(), 9);
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
        Person person = personHibernate.getById(userInfo.getPersonId());

        List<Reference> referencesOfCurrentReferenceType = referenceHibernate.getByReferenceTypeAndPerson(referenceType, person);

        return referencesOfCurrentReferenceType.size();
    }

    public boolean isPopularReferenceTypesNull() {
        return getPopularReferenceTypes().size() > 0;
    }

    public boolean isPopularReferenceTypesForPersonNull() {
        return getPopularReferenceTypesForPerson().size() > 0;
    }

    @Property
    private UploadedFile file;

    @Inject
    private RulebookHibernate rulebookHibernate;

    @Inject
    private SectionHibernate sectionHibernate;

    @Inject
    private ExcelWorkbook excelWorkbook;

    @Inject
    private AlertManager alertManager;

    @CommitAfter
    public void onSuccess() throws Exception {
        String fileName = file.getFileName();
        // todo make it work for other environments
        File copied = new File("C:\\Users\\Aleksandar\\code\\" + fileName);

        file.write(copied);
        readBiography(fileName);
        boolean success = copied.delete();

        if (success)
            alertManager.success("Успешно додадена биографија.");
        else
            alertManager.error("Неуспешно додадена биографија");
    }

    // todo rename reference type names. e.g. Projects - Проекти
    private void readBiography(String fileName) throws Exception {
        Person person = personHibernate.getById(userInfo.getPersonId());
        Rulebook rulebook = rulebookHibernate.getById(Long.valueOf(1));

        String sectionName = "Наставно-Образовна Дејност";
        Section section = sectionHibernate.getByColumn("name", sectionName).get(0);
        excelWorkbook.readNastavaSpreadsheet(fileName, 0, person, rulebook, section);

        sectionName = "Наставно-Истражувачка Дејност";
        section = sectionHibernate.getByColumn("name", sectionName).get(0);
        // read Projects sheet
        String referenceTypeName = "Projects";
        excelWorkbook.readSpreadsheet(fileName,
                1, referenceTypeName, 2,
                "Име на проектот", "ПОЕНИ", person, rulebook, section);

        sectionName = "Наставно-Истражувачка Дејност";
        section = sectionHibernate.getByColumn("name", sectionName).get(0);
        // read Papers sheet
        referenceTypeName = "Papers";
        excelWorkbook.readSpreadsheet(fileName,
                2, referenceTypeName, 2,
                "Автор 1", "//", person, rulebook, section);

        sectionName = "Наставно-Образовна Дејност";
        section = sectionHibernate.getByColumn("name", sectionName).get(0);
        // read Books sheet
        referenceTypeName = "Books";
        excelWorkbook.readSpreadsheet(fileName,
                3, referenceTypeName, 1,
                "Автори", "ПОЕНИ", person, rulebook, section);
    }
}
