package de.ddm.example;

import java.util.List;
import java.util.Map;

public class CheckData {
    public final List<Map<String, Object>> allTables;  // List of maps, each map contains column names and rows for a table

    public CheckData(List<Map<String, Object>> allTables) {
        this.allTables = allTables;
    }
}
