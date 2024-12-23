package de.ddm;

import akka.actor.typed.ActorSystem;
import de.ddm.actors.Guardian;
import de.ddm.actors.profiling.DependencyMiner;
import de.ddm.configuration.Command;
import de.ddm.configuration.SystemConfiguration;
import de.ddm.singletons.SystemConfigurationSingleton;

import java.io.IOException;

public class Main {

	public static void main(String[] args) {
		Command.applyOn(args);

		SystemConfiguration config = SystemConfigurationSingleton.get();

		ActorSystem<DependencyMiner.Message> system =
				ActorSystem.create(DependencyMiner.create(), "DependencyMinerSystem");

		// Start the mining process
		system.tell(new DependencyMiner.StartMiningMessage());

			}

	private static void waitForInput(String message) {
		try {
			System.out.println(message);
			System.in.read();
		} catch (IOException ignored) {
		}
	}
}
