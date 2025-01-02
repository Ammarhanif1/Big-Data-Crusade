package de.ddm.example;

import com.opencsv.CSVReader;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CsvReaderHelper {

    // Method to read CSV data and capture column names
    public static Map<String, Object> readCSVData(String csvFilePath) throws IOException, CsvValidationException {
        List<String> columnNames = new ArrayList<>();
        List<List<String>> data = new ArrayList<>();

        // Create a CSVParser to specify the delimiter
        try (FileReader fileReader = new FileReader(csvFilePath)) {
            CSVReader reader = new CSVReaderBuilder(fileReader)
                    .withCSVParser(new CSVParserBuilder().withSeparator(';').build()) // Specify semicolon as delimiter
                    .build();

            String[] nextLine;
            boolean firstRow = true; // Flag to capture the first row as column names

            while ((nextLine = reader.readNext()) != null) {
                if (firstRow) {
                    // Capture column names from the first row
                    columnNames = Arrays.asList(nextLine);
                    firstRow = false; // Skip the first row after capturing column names
                    continue;
                }

                // Add remaining rows as data
                List<String> row = Arrays.asList(nextLine);
                data.add(row);
            }
        }

        // Return both column names and rows
        Map<String, Object> tableData = new HashMap<>();
        tableData.put("Columns", columnNames); // Store column names
        tableData.put("Rows", data); // Store data rows
        return tableData;
    }
}
