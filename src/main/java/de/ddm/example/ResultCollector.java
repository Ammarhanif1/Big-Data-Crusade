package de.ddm.example;

import akka.actor.AbstractActor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ResultCollector extends AbstractActor {

    private BufferedWriter writer;

    @Override
    public void preStart() {
        try {
            // Open the file to write the results
            writer = new BufferedWriter(new FileWriter("results.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartMessage.class, msg -> {
                    // Start timing
//                    writer.write("Starting the Inclusion Dependency discovery process...\n");
                    writer.flush(); // Ensure it's written immediately
                })
                .match(FinalizeMessage.class, msg -> {
                    // End timing and print final result
//                    writer.write("Inclusion Dependency discovery process completed.\n");
                    writer.flush();
                    getContext().getSystem().terminate(); // Shut down the system
                })
                .match(WorkerResult.class, result -> {
                    // Write each worker's result to the file
                    if (result.isDependencyValid) {
                        writer.write(result.comparisonInfo + "\n");
                    } else {
                        writer.write(result.comparisonInfo + "\n");
                    }
                    writer.flush();
                })
                .build();
    }

    @Override
    public void postStop() {
        try {
            // Close the file writer after finishing
            if (writer != null) {
                writer.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
