package mk.ukim.finki.isis.edubio.services;

import mk.ukim.finki.isis.edubio.entities.*;
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
    private ReferenceTypeHibernate referenceTypeHibernate;

    @Inject
    ReferenceHibernate referenceHibernate;

    @Inject
    AttributeHibernate attributeHibernate;

    @Inject
    PersonHibernate personHibernate;

    @Inject
    ReferenceInputTemplateHibernate referenceInputTemplateHibernate;

    @Inject
    RulebookHibernate rulebookHibernate;

    // TODO add (secondary) english names of attributes for Papers ReferenceType.

    // helper variable. remembers parent name for category names
    private String parentCategoryName = "";

    private Integer columnNum = Integer.MAX_VALUE;
    private List<Attribute> attributes = new ArrayList<Attribute>();

    private String parentYear = "";
    private String parentSemester = "";

    public void readCategorySpreadsheet(String fileName, Integer spreadsheetNumber, Rulebook rulebook, Section section, ReferenceInputTemplate referenceInputTemplate) throws Exception{
        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        // 1st, 2nd and 3rd spreadsheets are categories
        XSSFSheet spreadsheet = workbook.getSheetAt(spreadsheetNumber);

        Iterator<Row> rowIterator = spreadsheet.iterator();

        storeReferenceTypes(rowIterator, rulebook, section, referenceInputTemplate);
    }

    private void storeReferenceTypes(Iterator<Row> rowIterator, Rulebook rulebook, Section section, ReferenceInputTemplate referenceInputTemplate) {
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            // ignore the first row for now. it contains category name.
            if (row.getRowNum() == 0) {
                continue;
            }

            List<String> rowValues = readCategoryRow(row);
            storeReferenceType(rowValues, rulebook, section, referenceInputTemplate);
        }
    }

    private List<String> readCategoryRow(XSSFRow row) {
        List<String> rowValues = new ArrayList<String>();

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            rowValues = readCategoryCell(rowValues, cell);
        }

        return rowValues;
    }

    private List<String> readCategoryCell(List<String> rowValues, Cell cell) {
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

        rowValues.add(cellValue);

        return rowValues;
    }

    private void storeReferenceType(List<String> rowValues, Rulebook rulebook, Section section, ReferenceInputTemplate referenceInputTemplate) {
        if (rowValues.size() > 0) {
            ReferenceType referenceType = createReferenceType(rowValues);

            persistReferenceType(referenceType, rulebook, section, referenceInputTemplate);
        }
    }

    private ReferenceType createReferenceType(List<String> rowValues) {
        ReferenceType referenceType = new ReferenceType();

        for (String cellValue : rowValues) {
            Integer columnIndex = rowValues.indexOf(cellValue);

            if (columnIndex.equals(0)) {
                referenceType.setName(cellValue);
            } else if (columnIndex.equals(1)) {
                referenceType.setPoints(Float.valueOf(cellValue));
            }
        }

        return referenceType;
    }

    private void persistReferenceType(ReferenceType referenceType, Rulebook rulebook, Section section, ReferenceInputTemplate referenceInputTemplate) {
        referenceTypeHibernate.store(referenceType);
        referenceTypeHibernate.setReferenceInputTemplate(referenceType, referenceInputTemplate);
        referenceTypeHibernate.setSection(referenceType, section, rulebook);
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

    public void readNastavaSpreadsheet(String fileName, Integer spreadsheetNumber, Person person, Rulebook rulebook, Section section) throws Exception{
        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);

        XSSFSheet spreadsheet = workbook.getSheetAt(spreadsheetNumber);
        Iterator<Row> rowIterator = spreadsheet.iterator();

        storeNastavaReferences(rowIterator, person, rulebook, section);
    }

    private void storeNastavaReferences(Iterator<Row> rowIterator, Person person, Rulebook rulebook, Section section) {
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            // ignore the first two rows for now.
            if (row.getRowNum() < 2) {
                continue;
            }

            if (row.getRowNum() == 2){
                readNastavaColumnNames(row);
            } else {
                List<String> rowValues = readNastavaRow(row);

                if (rowValues.size() == columnNum) {
                    createNastavaReference(rowValues, person, rulebook, section);
                }
            }
        }
    }

    private void readNastavaColumnNames(XSSFRow row) {
        // remember attribute names.
        getNastavaAttributes(row);
        // remember attributes size
        columnNum = attributes.size();
    }

    private void getNastavaAttributes(Row row) {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            String cellValue = parseNastavaCell(cell);

            if (cellValue.startsWith("Вкупно")){
                break;
            }

            Attribute attribute = createAttribute(cellValue);
            attributes.add(attribute);
        }
    }

    private void createNastavaReference(List<String> rowValues, Person person, Rulebook rulebook, Section section) {
        ReferenceType referenceType = getReferenceType("Одржување на настава - од прв циклус студии", rulebook, section);
        Reference reference = createReference(referenceType);
        personHibernate.setReference(reference, person, 1);

        for (String cellValue : rowValues) {
            Integer index = rowValues.indexOf(cellValue);
            Attribute attribute = attributes.get(index);

            boolean display = isDisplayAttribute(attribute);
            referenceHibernate.setAttributeValueIndexDisplay(reference, attribute, cellValue, index, display);
        }
    }

    private List<String> readNastavaRow(Row row) {
        List<String> rowValues = new ArrayList<String>();

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
        Integer columnIndex = cell.getColumnIndex();

        // a column can have string or numeric value
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cellValue = String.valueOf(cell.getNumericCellValue());
        }

        if (columnIndex.equals(0)) {
            if (cellValue.equals("")) {
                cellValue = parentYear;
            } else {
                parentYear = cellValue;
            }
        } else if (columnIndex.equals(1)) {
            if (cellValue.equals("")) {
                cellValue = parentSemester;
            } else {
                parentSemester = cellValue;
            }
        }

        return cellValue;
    }

    // Reads spreadsheets: projects, papers and books
    public void readSpreadsheet(String fileName, Integer spreadsheetNumber, String referenceTypeName, Integer startAtRow, String notNullColumnName, String stopReadingAtColumn, Person person, Rulebook rulebook, Section section) throws Exception{
        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        // 1, 2 and 3 spreadsheets are categories
        XSSFSheet spreadsheet = workbook.getSheetAt(spreadsheetNumber);
        Iterator<Row> rowIterator = spreadsheet.iterator();

        storeReferences(rowIterator, referenceTypeName, stopReadingAtColumn, startAtRow, notNullColumnName, person, rulebook, section);
    }

    private void storeReferences(Iterator<Row> rowIterator, String referenceTypeName, String stopReadingAtColumn, Integer startAtRow, String notNullColumnName, Person person, Rulebook rulebook, Section section) {
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            // ignore the first two rows for now.
            if (row.getRowNum() < startAtRow) {
                continue;
            }

            if (row.getRowNum() == startAtRow){
                readColumnNames(row, stopReadingAtColumn);
            } else {
                List<String> rowValues = readRow(row, notNullColumnName);

                if (rowValues.size() == columnNum) {
                    storeReference(rowValues, referenceTypeName, person, rulebook, section);
                }
            }
        }
    }

    private void readColumnNames(XSSFRow row, String stopReadingAtColumn) {
        // remember attribute names.
        getAttributes(row, stopReadingAtColumn);
        // remember attributes size
        columnNum = attributes.size();
    }

    private void storeReference(List<String> rowValues, String referenceTypeName, Person person, Rulebook rulebook, Section section) {
        Reference reference = persistReference(referenceTypeName, rowValues, rulebook, section);
        boolean hasAuthors = false;

        for (Attribute attribute : attributes) {
            String attributeName = attribute.getName();

            if (attributeName.startsWith("Автор")) {
                storeReferenceAuthor(rowValues, person, reference, attribute, attributeName);
                hasAuthors = true;
            }
        }

        // this sheet doesn't contain authors.
        if (!hasAuthors) {
            personHibernate.setReference(reference, person, 1);
        }
    }

    private void storeReferenceAuthor(List<String> rowValues, Person person, Reference reference, Attribute attribute, String attributeName) {
        String authorName = rowValues.get(attributes.indexOf(attribute));
        Integer authorNum;

        // empty cell
        if (authorName.equals("")) {
            return;
        }

        if (attributeName.contains(" ")){
            authorNum = Integer.valueOf(attributeName.split(" ")[1]);
        } else {
            authorNum = 1;
        }

        String bibtexAuthorName = buildBibtexName(authorName);
        person = findPerson(bibtexAuthorName);

        if (person == null) {
            personHibernate.setReference(reference, bibtexAuthorName, authorNum);
        } else {
            personHibernate.setReference(reference, person, authorNum);
        }
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

    private Reference persistReference(String referenceTypeName, List<String> rowValues, Rulebook rulebook, Section section) {
        ReferenceType referenceType = getReferenceType(referenceTypeName, rulebook, section);
        Reference reference = createReference(referenceType);

        addCellValuesToReference(rowValues, referenceType, reference);

        return reference;
    }

    private void addCellValuesToReference(List<String> rowValues, ReferenceType referenceType, Reference reference) {
        for (int i = 0; i < rowValues.size() ; i++) {
            Attribute attribute = attributes.get(i);

            // don't write authors as attributes
            if (attribute.getName().startsWith("Автор")) {
                continue;
            }

            boolean display = isDisplayAttribute(attribute);
            referenceHibernate.setAttributeValueIndexDisplay(reference, attribute, rowValues.get(i), i, display);
        }
    }

    private void getAttributes(Row row, String stopReadingAtColumn) {
        attributes = new ArrayList<Attribute>();

        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            String columnName = parseCell(cell);

            if (columnName.equals(stopReadingAtColumn)){
                break;
            }

            if (!columnName.equals("")) {
                Attribute attribute = createAttribute(columnName);

                attributes.add(attribute);
            }
        }
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

    private ReferenceType getReferenceType(String name, Rulebook rulebook, Section section) {
        List<ReferenceType> referenceTypes = referenceTypeHibernate.getByColumn("name", name);
        ReferenceType referenceType;

        if (referenceTypes.size() == 0) {
            referenceType = new ReferenceType();
            referenceType.setName(name);
            referenceTypeHibernate.store(referenceType);
            referenceTypeHibernate.setSection(referenceType, section, rulebook);

            addAttributesToReferenceType(referenceType);
        } else {
            referenceType = referenceTypes.get(0);
            // The referenceType is already created,
            // but it's attributes are not.
            if (referenceTypeHibernate.getAttributeValues(referenceType).size() == 1) {
                addAttributesToReferenceType(referenceType);
            }
        }

        return referenceType;
    }

    private void addAttributesToReferenceType(ReferenceType referenceType) {
        for (Attribute attribute : attributes) {
            // don't write authors as attributes
            if (attribute.getName().startsWith("Автор")) {
                continue;
            }

            boolean display = isDisplayAttribute(attribute);
            referenceTypeHibernate.setAttributeDisplay(referenceType, attribute, display);
        }
    }

    private Reference createReference(ReferenceType referenceType) {
        Reference reference = new Reference();
        reference.setReferenceType(referenceType);
        referenceHibernate.store(reference);

        return reference;
    }

    private List<String> readRow(Row row, String notNullColumnName) {
        List<String> rowValues = new ArrayList<String>();

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
