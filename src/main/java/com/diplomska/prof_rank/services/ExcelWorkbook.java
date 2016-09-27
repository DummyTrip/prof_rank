package com.diplomska.prof_rank.services;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Aleksandar on 27-Sep-16.
 */
public class ExcelWorkbook {
    private String originName = "";

    public List<List<String>> readCategorySpreadsheet(String fileName, Integer spreadsheetNumber) throws Exception{
        // List of row values of Category Spreadsheet.
        // Row values are a list of strings.
        List<List<String>> categoryValues = new ArrayList<List<String>>();

        FileInputStream fis = new FileInputStream(new File(fileName));

        XSSFWorkbook workbook = new XSSFWorkbook(fis);
        // 1, 2 and 3 spreadsheets are categories
        XSSFSheet spreadsheet = workbook.getSheetAt(spreadsheetNumber);

        Iterator<Row> rowIterator = spreadsheet.iterator();
        while (rowIterator.hasNext()) {
            XSSFRow row = (XSSFRow) rowIterator.next();

            List<String> rowValues = new ArrayList<String>();
            // uncomment to add row number to output
            String tmp = String.valueOf(row.getRowNum());
            rowValues.add(tmp);

            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                rowValues = readCategoryCells(rowValues, row, cell);
            }

            categoryValues.add(rowValues);
        }

        return categoryValues;
    }

    private List<String> readCategoryCells(List<String> rowValues, Row row, Cell cell) {
        Integer columnIndex = cell.getColumnIndex();

        if (columnIndex.equals(0)) {

            String cellValue = parseCategoryNameCell(cell);

            // ignore this row. it has an invalid name.
            if (cellValue.equals("")) {
                return new ArrayList<String>();
            }

            rowValues.add(cellValue);
        } else if (columnIndex.equals(1)) {
            // ignore this column and row. it has no name.
            if (rowValues.size() == 0) {
                return rowValues;
            }

            // this column can have string or numeric value
            if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                rowValues.add(cell.getStringCellValue());
            } else {
                rowValues.add(String.valueOf(cell.getNumericCellValue()));
            }
        }

        return rowValues;
    }

    private String parseCategoryNameCell(Cell cell) {
        String cellValue = cell.getStringCellValue();

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
            cellValue = originName + " " + cellValue;
        } else {
            // remember this name and add it
            // to next rows that starts with -
            originName = cellValue;
        }

        return cellValue;
    }
}
