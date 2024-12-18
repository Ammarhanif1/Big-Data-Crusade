package de.ddm;

import akka.actor.typed.ActorSystem;
import de.ddm.actors.Guardian;
import de.ddm.configuration.Command;
import de.ddm.configuration.SystemConfiguration;
import de.ddm.singletons.SystemConfigurationSingleton;
import de.ddm.utils.ResultsParser;

import java.io.IOException;
import java.util.Map;

public class main {

	public static void main(String[] args) {
		Command.applyOn(args);
		SystemConfiguration config = SystemConfigurationSingleton.get();

		final ActorSystem<Guardian.Message> guardian = ActorSystem.create(Guardian.create(), config.getActorSystemName(), config.toAkkaConfig());

		if (config.getRole().equals(SystemConfiguration.MASTER_ROLE)) {
			try {
				// Parse relationships from results.txt
				Map<String, String[]> relationships = ResultsParser.parseResultsFile("src/main/resources/data/results.txt");

				// Send relationships for validation
				relationships.forEach((relation, columns) ->
						guardian.tell(new Guardian.ValidateRelationshipsMessage(relation, columns))
				);

				// Process sample CSV files
				guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_customer.csv"));
				guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_orders.csv"));
				guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_lineitem.csv"));
				guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_nation.csv"));
				guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_part.csv"));
				guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_region.csv"));
				guardian.tell(new Guardian.ProcessCsvMessage("src/main/resources/data/tpch_supplier.csv"));






			} catch (IOException e) {
				e.printStackTrace();
			}

			guardian.tell(new Guardian.ShutdownMessage());
		}

		if (config.isStartPaused()) {
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
