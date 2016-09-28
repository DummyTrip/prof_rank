package com.diplomska.prof_rank.services;

import com.diplomska.prof_rank.entities.Attribute;
import com.diplomska.prof_rank.entities.Reference;
import com.diplomska.prof_rank.entities.ReferenceInstance;
import com.diplomska.prof_rank.entities.User;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.IntermediateType;

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
    
    // helper variable. remembers parent name for category names
    private String parentCategoryName = "";

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
                // add comment to stop storing references
                referenceHibernate.store(reference);
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

    public List<List<String>> readNastavaSpreadsheet(String fileName, Integer spreadsheetNumber) throws Exception{
        // List of row values of Category Spreadsheet.
        // Row values are a list of strings.
        List<List<String>> categoryValues = new ArrayList<List<String>>();

        FileInputStream fis = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        // 1, 2 and 3 spreadsheets are categories
        XSSFSheet spreadsheet = workbook.getSheetAt(spreadsheetNumber);

        Iterator<Row> rowIterator = spreadsheet.iterator();

        categoryValues = iterateAndStoreNastavaSpreadsheet(rowIterator, categoryValues);

        return categoryValues;
    }

    private Integer columnNum = Integer.MAX_VALUE;
    private List<Attribute> attributes = new ArrayList<Attribute>();

    private String parentYear = "";
    private String parentSemester = "";
    
    @Inject
    ReferenceInstanceHibernate referenceInstanceHibernate;
    
    @Inject
    AttributeHibernate attributeHibernate;

    @Inject
    UserHibernate userHibernate;

    private List<List<String>> iterateAndStoreNastavaSpreadsheet(Iterator<Row> rowIterator, List<List<String>> categoryValues) {
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            // ignore the first two rows for now.
            if (row.getRowNum() < 2) {
                continue;
            }

            if (row.getRowNum() == 2){
                // remember attribute names.
                // remember column index
                attributes = getAttributes(row);
                columnNum = attributes.size();
            } else {
                List<String> rowValues = new ArrayList<String>();

                ReferenceInstance referenceInstance = createReferenceInstance();

                rowValues = getAttributeValues(row, rowValues, referenceInstance);

//                if (rowValues.size() == attributes.size()) {
                if (rowValues.size() > 0) {
                    categoryValues.add(rowValues);
                }
            }
        }

        return categoryValues;
    }

    @CommitAfter
    private ReferenceInstance createReferenceInstance() {
        List<Reference> references = referenceHibernate.getByColumn("name", "Настава");
        Reference reference;
        if (references.size() == 0) {
            reference = new Reference();
            reference.setName("Настава");
            referenceHibernate.store(reference);
        } else {
            reference = references.get(0);
        }
        ReferenceInstance referenceInstance = new ReferenceInstance();
        referenceInstance.setReference(reference);
        referenceInstanceHibernate.store(referenceInstance);

        return referenceInstance;
    }

    @CommitAfter
    private List<Attribute> getAttributes(Row row) {
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

    private Attribute createAttribute(String cellValue) {
        List<Attribute> attributes = attributeHibernate.getByColumn("name", cellValue);
        Attribute attribute;
        if (attributes.size() == 0) {
            attribute = new Attribute();
            attribute.setName(cellValue);
            attribute.setInputType("text");
            attributeHibernate.store(attribute);
        } else {
            attribute = attributes.get(0);
        }

        return attribute;
    }

    @CommitAfter
    private List<String> getAttributeValues(Row row, List<String> rowValues, ReferenceInstance referenceInstance) {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();

            if (cell.getColumnIndex() == columnNum) {
                break;
            }

            String cellValue = parseNastavaCell(cell);
            rowValues.add(cellValue);

            Attribute attribute = attributes.get(rowValues.size() - 1);
            referenceInstanceHibernate.setAttributeValue(referenceInstance, attribute, cellValue);
        }

        return rowValues;
    }

    private List<String> getNastavaCell(List<String> rowValues, Cell cell) {
        Integer columnIndex = cell.getColumnIndex();

        String cellValue = parseNastavaCell(cell);

        rowValues.add(cellValue);

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
}
