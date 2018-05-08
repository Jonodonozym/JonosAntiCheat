
package jdz.jac.logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;
import lombok.AccessLevel;
import lombok.Getter;

public class LoggerConfig implements Listener {
	@Getter(value = AccessLevel.PACKAGE) private static boolean newFile = false;
	@Getter(value = AccessLevel.PACKAGE) private static boolean fileLogging = true;
	@Getter(value = AccessLevel.PACKAGE) private static boolean consoleLogging = true;

	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(JAC.getInstance()))
			return;

		FileConfiguration config = event.getConfig();

		newFile = config.getBoolean("logging.singleFile");
		fileLogging = config.getBoolean("logging.file");
		consoleLogging = config.getBoolean("logging.console");

		for (Logger logger : Logger.getAll()) {
			logger.setNewLog(newFile);
			logger.setWriteToLog(fileLogging);
			logger.setPrintToConsole(consoleLogging);
		}
	}
}
