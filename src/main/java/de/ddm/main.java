package de.ddm;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.opencsv.exceptions.CsvValidationException;
import de.ddm.example.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        // Set the paths for your CSV files
        String partCsvFilePath = "data/TPCH/tpch_part.csv";
        String customerCsvFilePath = "data/TPCH/tpch_customer.csv";
        String lineCsvFilePath = "data/TPCH/tpch_lineitem.csv";

        try {
            // Read data from CSV files
            Map<String, Object> partData = CsvReaderHelper.readCSVData(partCsvFilePath);
            Map<String, Object> customerData = CsvReaderHelper.readCSVData(customerCsvFilePath);
            Map<String, Object> lineData = CsvReaderHelper.readCSVData(lineCsvFilePath);

            // Print the data to verify it's being read correctly
            System.out.println("Part Data: " + ((List<?>) partData.get("Rows")).size() + " rows");
            System.out.println("Customer Data: " + ((List<?>) customerData.get("Rows")).size() + " rows");
            System.out.println("Line Data: " + ((List<?>) lineData.get("Rows")).size() + " rows");

            // Add all tables to a list
            List<Map<String, Object>> allTables = new ArrayList<>();
            allTables.add(partData);  // Table A (e.g., tpch_part.csv)
            allTables.add(customerData);  // Table B (e.g., tpch_customer.csv)
            allTables.add(lineData);  // Table C (e.g., tpch_lineitem.csv)

            // Create the actor system
            ActorSystem actorSystem = ActorSystem.create("InclusionDependencySystem");

            // Create the ResultCollector actor (to manage output and time)
            ActorRef resultCollector = actorSystem.actorOf(Props.create(ResultCollector.class), "resultCollector");

            // Create the DependencyMiner actor (for aggregation of results)
            ActorRef minerActor = actorSystem.actorOf(Props.create(DependencyMiner.class), "minerActor");

            // Create the Master actor
            ActorRef masterActor = actorSystem.actorOf(Props.create(MasterActor.class, minerActor), "masterActor");

            // Start the process by sending the data to the Master actor
            masterActor.tell(new CheckData(allTables), ActorRef.noSender());

            // Send StartMessage to start the timing
            resultCollector.tell(new StartMessage(), ActorRef.noSender());

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
