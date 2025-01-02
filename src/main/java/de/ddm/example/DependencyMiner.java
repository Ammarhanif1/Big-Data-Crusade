package de.ddm.example;

import akka.actor.AbstractActor;

public class DependencyMiner extends AbstractActor {

    private int validCount = 0;
    private int totalWorkers = 1;
    private boolean dependencyValid = true;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(WorkerResult.class, result -> {
                    if (!result.isDependencyValid) {
                        dependencyValid = false;
                        System.out.println("Inclusion Dependency Check Failed: " + result.comparisonInfo);
                    }

                    validCount++;

                    if (validCount == totalWorkers) {
                        // If all workers have responded, finalize and print the result
                        System.out.println("Inclusion Dependency Valid: " + dependencyValid);
                        getContext().getSystem().actorSelection("/user/resultCollector").tell(new FinalizeMessage(), getSelf());
                        getContext().getSystem().terminate();
                    }
                })
                .build();
    }
}
