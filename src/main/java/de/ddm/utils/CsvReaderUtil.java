package de.ddm.utils;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class CsvReaderUtil {

    public static List<String[]> readCsv(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                data.add(nextLine);
            }
            System.out.println("Read " + data.size() + " rows from " + filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
