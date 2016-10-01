package com.diplomska.prof_rank.pages;

import com.diplomska.prof_rank.entities.*;
import com.diplomska.prof_rank.services.ExcelWorkbook;
import com.diplomska.prof_rank.services.ReferenceHibernate;
import com.diplomska.prof_rank.services.ReferenceTypeHibernate;
import com.diplomska.prof_rank.services.UserHibernate;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.hibernate.Session;
import org.w3c.dom.Attr;

import java.util.List;

/**
 * Created by Aleksandar on 23-Sep-16.
 */
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
        return excelWorkbook.readSpreadsheet(fileName,
                spreadsheetNumber, referenceName, startAtRow,
                notNullColumnName, stopReadingAtColumn);

//        // 1, 2 or 3 spreadsheet number
//        return excelWorkbook.readCategorySpreadsheet(fileName, 1);
//
//        return excelWorkbook.readNastavaSpreadsheet(fileName, 4);
    }
}
