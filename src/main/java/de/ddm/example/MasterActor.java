package de.ddm.example;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class MasterActor extends AbstractActor {
    private final ActorRef minerActor;

    public MasterActor(ActorRef minerActor) {
        this.minerActor = minerActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CheckData.class, data -> {
                    getContext().getSystem().actorSelection("/user/ResultCollector").tell(new StartMessage(), getSelf());

                    ActorRef worker = getContext().actorOf(Props.create(DependencyWorker.class));
                    worker.tell(new CheckData(data.allTables), getSelf());
                })
                .match(WorkerResult.class, result -> {
                    minerActor.tell(result, getSelf());
                })
                .build();
    }
}
