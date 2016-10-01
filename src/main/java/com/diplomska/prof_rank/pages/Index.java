package com.diplomska.prof_rank.pages;


import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.entities.Section;
import com.diplomska.prof_rank.entities.User;
import com.diplomska.prof_rank.services.ExcelWorkbook;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceInstanceHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.util.ArrayList;
import java.util.List;

/**
 * Start page of application prof_rank.
 */
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

    @Property
    Reference reference;

    public List<Reference> getReferences() {
        return referenceHibernate.getAll(9);
    }

    public Section getSection() {
        return referenceHibernate.getSections(reference).get(0);
    }

    public Integer getNumberOfReferenceInstances() {
        User user = userHibernate.getById(Long.valueOf(1));
//        List<ReferenceInstance> referenceInstancesOfCurrentReference = referenceInstanceHibernate.getByReferenceAndUser(reference, user);
        List<ReferenceInstance> referenceInstancesOfCurrentReference = referenceInstanceHibernate.getByReference(reference);

        return referenceInstancesOfCurrentReference.size();
    }

    @CommitAfter
    void onActionFromAddReferencesFromExcel() throws Exception{
        String fileName = "poi_test.xlsx";

        excelWorkbook.readCategorySpreadsheet(fileName, 1);
        excelWorkbook.readCategorySpreadsheet(fileName, 2);
        excelWorkbook.readCategorySpreadsheet(fileName, 3);

        excelWorkbook.readNastavaSpreadsheet(fileName, 4);
        
        // read Projects sheet
        excelWorkbook.readSpreadsheet(fileName,
                5, "Projects", 2,
                "Име на проектот", "ПОЕНИ");

        // read Papers sheet
        excelWorkbook.readSpreadsheet(fileName,
                6, "Papers", 2,
                "Автор 1", "//");

        // read Books sheet
        excelWorkbook.readSpreadsheet(fileName,
                7, "Books", 1,
                "Автори", "ПОЕНИ");
    }
}
