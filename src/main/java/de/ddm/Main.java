package de.ddm;

import akka.actor.typed.ActorSystem;
import de.ddm.actors.Guardian;
import de.ddm.configuration.Command;
import de.ddm.configuration.SystemConfiguration;
import de.ddm.singletons.SystemConfigurationSingleton;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		Command.applyOn(args);

		SystemConfiguration config = SystemConfigurationSingleton.get();

		final ActorSystem<Guardian.Message> guardian = ActorSystem.create(Guardian.create(), config.getActorSystemName(), config.toAkkaConfig());

		if (config.getRole().equals(SystemConfiguration.MASTER_ROLE)) {

			guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_customer.csv"));
			guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_orders.csv"));
			guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_lineitem.csv"));

			guardian.tell(new Guardian.ShutdownMessage());
		}
		if (config.getRole().equals(SystemConfiguration.MASTER_ROLE)) {
			if (config.isStartPaused())
				waitForInput(">>> Press ENTER to start <<<");

			guardian.tell(new Guardian.StartMessage());

			waitForInput(">>> Press ENTER to exit <<<");

			guardian.tell(new Guardian.ShutdownMessage());
		}
	}

	private static void waitForInput(String message) {
		try {
			System.out.println(message);
			System.in.read();
		} catch (IOException ignored) {
		}
	}
}
