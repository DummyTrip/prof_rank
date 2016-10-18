package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.*;
import mk.ukim.finki.isis.model.entities.Person;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tapestry5.ioc.annotations.Inject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Aleksandar on 27-Sep-16.
 */
public class ExcelWorkbook {
    @Inject
    private ReferenceHibernate referenceHibernate;

    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @Inject
    UserHibernate userHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Inject
    ReferenceTypeHibernate referenceTypeHibernate;
    
    // helper variable. remembers parent name for category names
    private String parentCategoryName = "";

    private Integer columnNum = Integer.MAX_VALUE;
    private List<Attribute> attributes = new ArrayList<Attribute>();

    private String parentYear = "";
    private String parentSemester = "";

    public List<List<String>> readCategorySpreadsheet(String fileName, Integer spreadsheetNumber) throws Exception{
        // List of row values of Category Spreadsheet.
        // Row values are a list of strings.
        List<List<String>> categoryValues = new ArrayList<List<String>>();

        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        // 1, 2 and 3 spreadsheets are categories
        XSSFSheet spreadsheet = workbook.getSheetAt(spreadsheetNumber);

        Iterator<Row> rowIterator = spreadsheet.iterator();

        categoryValues = iterateAndStoreCategorySpreadsheet(rowIterator, categoryValues);

        return categoryValues;
    }

    private List<List<String>> iterateAndStoreCategorySpreadsheet(Iterator<Row> rowIterator, List<List<String>> categoryValues) {
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            // ignore the first row for now. it contains category name.
            if (row.getRowNum() == 0) {
                continue;
            }

            List<String> rowValues = new ArrayList<String>();
            Reference reference = new Reference();
            // uncomment to add row number to output
//            String tmp = String.valueOf(row.getRowNum());
//            rowValues.add(tmp);

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                rowValues = storeCategoryCell(rowValues, cell, reference);
            }
            if (rowValues.size() > 0) {
                categoryValues.add(rowValues);
                // comment next lines to stop storing references
                ReferenceType referenceType = referenceTypeHibernate.getById(Long.valueOf(1));
                referenceHibernate.store(reference);
                referenceHibernate.setReferenceType(reference, referenceType);
            }
        }

        return categoryValues;
    }


    private List<String> storeCategoryCell(List<String> rowValues, Cell cell, Reference reference) {
        Integer columnIndex = cell.getColumnIndex();

        // ignore this column and row. it has no name.
        if (columnIndex > 0 && rowValues.size() == 0) {
            return new ArrayList<String>();
        }

        String cellValue = parseCategoryCell(cell);

        // ignore this row. it has an invalid name.
        if (cellValue.equals("") || cellValue.length() == 0) {
            return new ArrayList<String>();
        }

        if (columnIndex.equals(0)) {
            reference.setName(cellValue);
        } else if (columnIndex.equals(1)) {
            reference.setPoints(Float.valueOf(cellValue));
        }

        rowValues.add(cellValue);

        return rowValues;
    }

    private String parseCategoryCell(Cell cell) {
        String cellValue = "";
        // a column can have string or numeric value
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cellValue = String.valueOf(cell.getNumericCellValue());
        }

        // specific parse for category name column
        if (cell.getColumnIndex() == 0) {
            cellValue = parseCategoryNameCell(cellValue);
        }

        return cellValue;
    }

    private String parseCategoryNameCell(String cellValue) {
        if (cellValue.startsWith("[") || cellValue.length() == 0) {
            //ignore this row. it has no name
            return "";
        } else if (cellValue.endsWith("]")) {
            cellValue = cellValue.substring(0, cellValue.length() - 3);
        } else if (cellValue.matches(".*\\d$")) {
            // some cells end in a reference number. ignore it.
            cellValue = cellValue.substring(0, cellValue.length() - 1);
        }
        if (cellValue.startsWith("-")) {
            // add name from previous row
            cellValue = parentCategoryName + " " + cellValue;
        } else {
            // remember this name and add it
            // to next rows that starts with -
            parentCategoryName = cellValue;
        }

        return cellValue;
    }

    public List<List<String>> readNastavaSpreadsheet(String fileName, Integer spreadsheetNumber, Person person) throws Exception{
        // List of row values of Category Spreadsheet.
        // Row values are a list of strings.
        List<List<String>> categoryValues = new ArrayList<List<String>>();

        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        // 1, 2 and 3 spreadsheets are categories
        XSSFSheet spreadsheet = workbook.getSheetAt(spreadsheetNumber);

        Iterator<Row> rowIterator = spreadsheet.iterator();

        categoryValues = iterateAndStoreNastavaSpreadsheet(rowIterator, categoryValues, person);

        return categoryValues;
    }

    private List<List<String>> iterateAndStoreNastavaSpreadsheet(Iterator<Row> rowIterator, List<List<String>> categoryValues, Person person) {
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            // ignore the first two rows for now.
            if (row.getRowNum() < 2) {
                continue;
            }

            if (row.getRowNum() == 2){
                // remember attribute names.
                // remember column index
                attributes = getNastavaAttributes(row);
                columnNum = attributes.size();
            } else {
                List<String> rowValues = new ArrayList<String>();

                rowValues = getNastavaAttributeValues(row, rowValues);

                if (rowValues.size() == attributes.size()) {
                    Reference reference = getReference("Одржување на настава - од прв циклус студии");
                    ReferenceInstance referenceInstance = createReferenceInstance(reference);
                    personHibernate.setReferenceInstance(person, referenceInstance, 1);
                    for (String cellValue : rowValues) {
                        Integer index = rowValues.indexOf(cellValue);
                        Attribute attribute = attributes.get(index);

                        boolean display = isDisplayAttribute(attribute);
                        referenceInstanceHibernate.setAttributeValueIndexDisplay(referenceInstance, attribute, cellValue, index, display);
                    }
                    categoryValues.add(rowValues);
                }
            }
        }

        return categoryValues;
    }

    private boolean isDisplayAttribute(Attribute attribute) {
        String attributeName = attribute.getName();
        if (attributeName.equals("Наслов") ||
                attributeName.equals("Предмет") ||
                attributeName.equals("Име на проектот") ||
                attributeName.startsWith("Период") ||
                attributeName.equals("Год.") ||
                attributeName.startsWith("Позиција") ||
                attributeName.equals("Година"))
        {
            return true;
        } else {
            return false;
        }
    }

    private List<Attribute> getNastavaAttributes(Row row) {
        List<Attribute> rowValues = new ArrayList<Attribute>();

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            String cellValue = parseNastavaCell(cell);

            if (cellValue.startsWith("Вкупно")){
                break;
            }

            Attribute attribute = createAttribute(cellValue);

            rowValues.add(attribute);
        }
        
        return rowValues;
    }

    private List<String> getNastavaAttributeValues(Row row, List<String> rowValues) {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            if (cell.getColumnIndex() == columnNum) {
                break;
            }

            String cellValue = parseNastavaCell(cell);
            if (cellValue.equals("ВКУПНО")){
                return new ArrayList<String>();
            }
            rowValues.add(cellValue);
        }

        return rowValues;
    }

    private String parseNastavaCell(Cell cell) {
        String cellValue = "";
        // a column can have string or numeric value
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cellValue = String.valueOf(cell.getNumericCellValue());
        }

        if (cell.getColumnIndex() == 0) {
            if (cellValue.equals("")) {
                cellValue = parentYear;
            } else {
                parentYear = cellValue;
            }
        } else if (cell.getColumnIndex() == 1) {
            if (cellValue.equals("")) {
                cellValue = parentSemester;
            } else {
                parentSemester = cellValue;
            }
        }


        return cellValue;
    }

    // Reads spreadsheets: projects, papers and books
    public List<List<String>> readSpreadsheet(String fileName, Integer spreadsheetNumber, String referenceName, Integer startAtRow, String notNullColumnName, String stopReadingAtColumn, Person person) throws Exception{
        // Row values are a list of strings.
        List<List<String>> categoryValues = new ArrayList<List<String>>();

        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        // 1, 2 and 3 spreadsheets are categories
        XSSFSheet spreadsheet = workbook.getSheetAt(spreadsheetNumber);

        Iterator<Row> rowIterator = spreadsheet.iterator();

        categoryValues = iterateAndStoreSpreadsheet(rowIterator, categoryValues, referenceName, stopReadingAtColumn, startAtRow, notNullColumnName, person);

        return categoryValues;
    }

    private List<List<String>> iterateAndStoreSpreadsheet(Iterator<Row> rowIterator, List<List<String>> categoryValues, String referenceName, String stopReadingAtColumn, Integer startAtRow, String notNullColumnName, Person person) {
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            // ignore the first two rows for now.
            if (row.getRowNum() < startAtRow) {
                continue;
            }

            if (row.getRowNum() == startAtRow){
                // remember attribute names.
                // remember column index
                attributes = getAttributes(row, stopReadingAtColumn);
                columnNum = attributes.size();
            } else {
                List<String> rowValues = new ArrayList<String>();

                rowValues = getAttributeValues(row, rowValues, notNullColumnName);

                if (rowValues.size() == attributes.size()) {
                    categoryValues.add(rowValues);

                    boolean hasAuthors = false;
                    for (Attribute attribute : attributes) {
                        String attrubuteName = attribute.getName();
                        if (attrubuteName.startsWith("Автор")) {
                            String authorName = rowValues.get(attributes.indexOf(attribute));

                            // empty cell
                            if (authorName.equals("")) {
                                continue;
                            }

                            Integer authorNum;
                            if (attrubuteName.contains(" ")){
                                authorNum = Integer.valueOf(attrubuteName.split(" ")[1]);
                            } else {
                                authorNum = 1;
                            }

                            String bibtexAuthorName = buildBibtexName(authorName);
                            person = findPerson(bibtexAuthorName);

                            if (person == null) {
                                persistReferenceInstance(referenceName, bibtexAuthorName, rowValues, authorNum);
                            } else {
                                persistReferenceInstance(referenceName, person, rowValues, authorNum);
                            }

                            hasAuthors = true;
                        }
                    }

                    // this sheet doesnt contain authors.
                    if (!hasAuthors) {
                        persistReferenceInstance(referenceName, person, rowValues, 1);
                    }
                }
            }
        }

        return categoryValues;
    }

    private Person findPerson(String bibtexAuthorName) {
        List<Person> persons = personHibernate.getByBibtexAuthorName(bibtexAuthorName);

        if (persons.size() > 0){
            return persons.get(0);
        } else {
            return null;
        }
    }

    private String buildBibtexName(String authorName) {
        String[] fullName = authorName.split(" ");
        Integer firstNameIndex = 0;
        Integer lastNameIndex = fullName.length - 1;

        return fullName[lastNameIndex] + ", " + fullName[firstNameIndex];
    }

    private void persistReferenceInstance(String referenceName, Person person, List<String> rowValues, Integer authorNum) {
        Reference reference = getReference(referenceName);
        ReferenceInstance referenceInstance = createReferenceInstance(reference);
        personHibernate.setReferenceInstance(person, referenceInstance, authorNum);

        addCellValuesToReferenceInstance(rowValues, reference, referenceInstance);
    }

    private void persistReferenceInstance(String referenceName, String author, List<String> rowValues, Integer authorNum) {
        Reference reference = getReference(referenceName);
        ReferenceInstance referenceInstance = createReferenceInstance(reference);
        personHibernate.setReferenceInstance(referenceInstance, author, authorNum);

        addCellValuesToReferenceInstance(rowValues, reference, referenceInstance);
    }

    private void addCellValuesToReferenceInstance(List<String> rowValues, Reference reference, ReferenceInstance referenceInstance) {
        for (int i = 0; i < rowValues.size() ; i++) {
            Attribute attribute = attributes.get(i);

            // don't write authors as attributes
            if (attribute.getName().startsWith("Автор")) {
                continue;
            }

            boolean display = isDisplayAttribute(attribute);
            referenceInstanceHibernate.setAttributeValueIndexDisplay(referenceInstance, attribute, rowValues.get(i), i, display);
        }
    }

    private List<Attribute> getAttributes(Row row, String stopReadingAtColumn) {
        List<Attribute> rowValues = new ArrayList<Attribute>();

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            String columnName = parseCell(cell);

            if (columnName.equals(stopReadingAtColumn)){
                break;
            }

            if (!columnName.equals("")) {
                Attribute attribute = createAttribute(columnName);

                rowValues.add(attribute);
            }
        }

        return rowValues;
    }

    private Attribute createAttribute(String columnName) {
        List<Attribute> attributes = attributeHibernate.getByColumn("name", columnName);
        Attribute attribute;
        if (attributes.size() == 0) {
            attribute = new Attribute();
            attribute.setName(columnName);
            attribute.setInputType("text");
            attributeHibernate.store(attribute);
        } else {
            attribute = attributes.get(0);
        }

        return attribute;
    }

    private Reference getReference(String name) {
        List<Reference> references = referenceHibernate.getByColumn("name", name);
        Reference reference;
        // second part of the clause is used when the reference is already created,
        // but it's attributes are not.
        if (references.size() == 0) {
            reference = new Reference();
            reference.setName(name);
            referenceHibernate.store(reference);

            addAttributesToReference(reference);
        } else {
            reference = references.get(0);

            if (referenceHibernate.getAttributeValues(reference).size() == 1) {
                addAttributesToReference(reference);
            }
        }

        return reference;
    }

    private void addAttributesToReference(Reference reference) {
        for (Attribute attribute : attributes) {
            // don't write authors as attributes
            if (attribute.getName().startsWith("Автор")) {
                continue;
            }

            boolean display = isDisplayAttribute(attribute);
            referenceHibernate.setAttributeDisplay(reference, attribute, display);
        }
    }

    private ReferenceInstance createReferenceInstance(Reference reference) {
        ReferenceInstance referenceInstance = new ReferenceInstance();
        referenceInstance.setReference(reference);
        referenceInstanceHibernate.store(referenceInstance);

        User user = userHibernate.getById(Long.valueOf(1));
        userHibernate.setReferenceInstance(user, referenceInstance);

//        Person person = personHibernate.getById(Long.valueOf(1));
//        personHibernate.setReferenceInstance(person, referenceInstance);

        return referenceInstance;
    }

    private List<String> getAttributeValues(Row row, List<String> rowValues, String notNullColumnName) {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            if (cell.getColumnIndex() == columnNum) {
                break;
            }

            String cellValue = parseCell(cell);

            // this cell must not be empty
            if (attributes.get(rowValues.size()).getName().equals(notNullColumnName)) {
                if (cellValue.equals("") || cellValue.length() == 0) {
                    return new ArrayList<String>();
                }
            }

            rowValues.add(cellValue);
        }

        return rowValues;
    }

    private String parseCell(Cell cell) {
        String cellValue = "";
        // a column can have string or numeric value
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cellValue = String.valueOf(cell.getNumericCellValue());
        }

        return cellValue;
    }


}
