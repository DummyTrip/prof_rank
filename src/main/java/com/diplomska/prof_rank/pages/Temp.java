package com.diplomska.prof_rank.pages;

import com.diplomska.prof_rank.annotations.PublicPage;
import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.*;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.w3c.dom.Attr;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
@PublicPage
public class Temp {
    @Property
    private Reference tmp;

    @Inject
    ReferenceHibernate referenceHibernate;

    public List<Reference> getTemp() {
        return referenceHibernate.getAll();
    }

    @Property
    private Attribute attribute;

    @Inject
    private ReferenceTypeHibernate referenceTypeHibernate;

    public List<Attribute> getAttributes() {
        ReferenceType referenceType = referenceHibernate.getReferenceType(tmp);
        return referenceTypeHibernate.getAttributes(referenceType);
    }

    public boolean isTextInput() {
        return attribute.getInputType().equals("text") ? true :false;
    }

    @Property
    private String testVal;

    @Inject
    private ExcelWorkbook excelWorkbook;

    @Property
    private List<String> po;

    public String getP() {
        String tmp = "";
        for (String pop : po) {
            tmp += " || " + pop;
        }

        return tmp;
    }

//    @CommitAfter
    public List<List<String>> getPoi() throws  Exception{
        String fileName            = "poi_test.xlsx";
        Integer spreadsheetNumber  = 6;
        String referenceName       = "Papers";
        Integer startAtRow         = 2;
        String notNullColumnName   = "Автор 1";
        String stopReadingAtColumn = "//";

        // Reads projects, papers and books spreadsheets.
        // 5, 6 or 7 spreadsheet number
//        return excelWorkbook.readSpreadsheet(fileName,
//                spreadsheetNumber, referenceName, startAtRow,
//                notNullColumnName, stopReadingAtColumn);

//        // 1, 2 or 3 spreadsheet number
//        return excelWorkbook.readCategorySpreadsheet(fileName, 1);
//
//        return excelWorkbook.readNastavaSpreadsheet(fileName, 4);

        return new ArrayList<List<String>>();
    }

    @Property
    int pageNumber;

    private static final int PageSize = 30;

    @Property
    private ReferenceInstance value;

    @Inject
    Session session;

    @Persist
    @Property
    List<ReferenceInstance> values;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Property
    AttributeReferenceInstance ari;

    public String getDisplayName() {
        return referenceInstanceHibernate.getDisplayName(value);
    }

    public List<AttributeReferenceInstance> getAttributeValues() {
        return value.getAttributeReferenceInstances();
    }

    public void setupRender() {
        values = new ArrayList<ReferenceInstance>();
    }

    public String getZone() {
        if (pageNumber > 0) {
            return "zone" + pageNumber;
        } else {
            return "zone";
        }
    }

    @OnEvent("nextPage")
    List<ReferenceInstance> moreValues() throws InterruptedException {
        int first = Integer.valueOf(pageNumber) * PageSize;

        Thread.sleep(2000);

        values.addAll(referenceInstanceHibernate.getAll(first, PageSize));

        return values;
    }

}
