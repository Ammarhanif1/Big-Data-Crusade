package de.ddm.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ResultsParser {

    public static Map<String, String[]> parseResultsFile(String filePath) throws IOException {
        Map<String, String[]> relationships = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("->")) {
                    String[] parts = line.split("->|:");
                    String leftTable = parts[0].trim();
                    String rightTable = parts[1].trim();
                    String leftColumn = parts[2].trim().replace("[", "").replace("]", "");
                    String rightColumn = parts[3].trim().replace("[", "").replace("]", "");

                    relationships.put(leftTable + " -> " + rightTable, new String[]{leftTable, leftColumn, rightTable, rightColumn});
                }
            }
        }
        return relationships;
    }
}
