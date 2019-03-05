
package jdz.jac.logger;

import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.configuration.AutoConfig;
import jdz.bukkitUtils.configuration.ConfigReloadEvent;
import lombok.Getter;

public class LoggerConfig extends AutoConfig {
	@Getter private static boolean singleFile = false;
	@Getter private static boolean file = true;
	@Getter private static boolean console = true;

	public LoggerConfig(Plugin plugin) {
		super(plugin, "logging");
	}

	@Override
	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		super.onConfigReload(event);

		for (Logger logger : Logger.getAll()) {
			logger.setNewLog(singleFile);
			logger.setWriteToLog(file);
			logger.setPrintToConsole(console);
		}
	}
}
