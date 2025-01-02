package de.ddm.example;

import akka.actor.AbstractActor;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DependencyWorker extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CheckData.class, data -> {
                    // Check inclusion dependencies for all tables and columns
                    boolean isValid = checkInclusionDependency(data.allTables);
                    // Send result to ResultCollector
                    getSender().tell(new WorkerResult(isValid, "All columns comparison complete."), getSelf());
                })
                .build();
    }

    // Method to check if all values in one table's columns are present in another table's columns
    private boolean checkInclusionDependency(List<Map<String, Object>> allTables) {
        boolean isValid = true;

        // Table names for reference (could be extended to dynamically pull names from CSV)
        String[] tableNames = { "tpch_part", "tpch_customer", "tpch_lineitem", "tpch_supplier", "tpch_nation"};

        // Iterate over all pairs of tables
        for (int i = 0; i < allTables.size(); i++) {
            Map<String, Object> tableAData = allTables.get(i);
            List<String> columnNames = (List<String>) tableAData.get("Columns");  // Get column names for tableA
            List<List<String>> tableA = (List<List<String>>) tableAData.get("Rows");  // Get rows for tableA

            // Compare each column in tableA with other columns in the same tableA (same table)
            for (int columnA = 0; columnA < columnNames.size(); columnA++) {
                String columnAName = columnNames.get(columnA); // Use actual column name from CSV
                Set<String> setA = new HashSet<>();

                // Add all values of columnA to setA
                for (List<String> row : tableA) {
                    setA.add(row.get(columnA).trim().toLowerCase()); // Normalize case and trim
                }

                // Now compare columnA with every other column in the same tableA
                for (int columnB = 0; columnB < columnNames.size(); columnB++) {
                    if (columnA == columnB) continue; // Skip comparing the column to itself

                    String columnBName = columnNames.get(columnB); // Use actual column name from CSV

                    // Check if all values in columnB are in setA
                    for (List<String> row : tableA) {
                        String item = row.get(columnB).trim().toLowerCase(); // Normalize case and trim
                        if (!setA.contains(item)) {
                            // Log the result and send it to ResultCollector
//                            String resultMessage = tableNames[i] + " -> " + tableNames[i] + ": [" + columnAName + "] c [" + columnBName + "]";
//                            System.out.println(resultMessage); // Also print for debugging
//                            getContext().getSystem().actorSelection("/user/resultCollector")
//                                    .tell(new WorkerResult(false, resultMessage), getSelf());
                            isValid = false;  // If any item in columnB is not in setA, mark as invalid
                        } else {
                            // Log the result and send it to ResultCollector
                            String resultMessage = tableNames[i] + " -> " + tableNames[i] + ": [" + columnAName + "] ⊂ [" + columnBName + "]";
                            System.out.println(resultMessage); // Also print for debugging
                            getContext().getSystem().actorSelection("/user/resultCollector")
                                    .tell(new WorkerResult(true, resultMessage), getSelf());
                        }
                    }
                }
            }

            // Compare columns in tableA with columns in all other tables (tableB)
            for (int j = i + 1; j < allTables.size(); j++) {
                Map<String, Object> tableBData = allTables.get(j);
                List<String> columnNamesB = (List<String>) tableBData.get("Columns");  // Get column names for tableB
                List<List<String>> tableB = (List<List<String>>) tableBData.get("Rows");  // Get rows for tableB

                // Iterate over columns of tableA and compare with all columns of tableB
                for (int columnA = 0; columnA < columnNames.size(); columnA++) {
                    String columnAName = columnNames.get(columnA);
                    Set<String> setA = new HashSet<>();

                    // Add all values of columnA to setA
                    for (List<String> row : tableA) {
                        setA.add(row.get(columnA).trim().toLowerCase()); // Normalize case and trim
                    }

                    // Compare with every column in tableB
                    for (int columnB = 0; columnB < columnNamesB.size(); columnB++) {
                        String columnBName = columnNamesB.get(columnB);

                        // Check if all values in columnB are in setA
                        for (List<String> row : tableB) {
                            String item = row.get(columnB).trim().toLowerCase(); // Normalize case and trim
                            if (!setA.contains(item)) {
                                // Log the result and send it to ResultCollector
//                                String resultMessage = tableNames[i] + " -> " + tableNames[j] + ": [" + columnAName + "] c [" + columnBName + "]";
//                                System.out.println(resultMessage); // Also print for debugging
//                                getContext().getSystem().actorSelection("/user/resultCollector")
//                                        .tell(new WorkerResult(false, resultMessage), getSelf());
                                isValid = false;
                            } else {
                                // Log the result and send it to ResultCollector
                                String resultMessage = tableNames[i] + " -> " + tableNames[j] + ": [" + columnAName + "] ⊂ [" + columnBName + "]";
                                System.out.println(resultMessage); // Also print for debugging
                                getContext().getSystem().actorSelection("/user/resultCollector")
                                        .tell(new WorkerResult(true, resultMessage), getSelf());
                            }
                        }
                    }
                }
            }
        }

        return isValid; // Return whether all inclusion dependencies are satisfied
    }
}
