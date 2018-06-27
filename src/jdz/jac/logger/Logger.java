
package jdz.jac.logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.jac.JAC;
import jdz.jac.detection.HackEvent;
import jdz.jac.punisher.AutobanEvent;
import lombok.Getter;

public class Logger extends FileLogger {
	private static final Map<String, Logger> loggers = new HashMap<String, Logger>();
	@Getter private static final Logger general = new Logger("Hack Log");

	public static Logger get(String logName) {
		if (!loggers.containsKey(logName))
			loggers.put(logName, new Logger(logName));
		return loggers.get(logName);
	}

	public static Collection<Logger> getAll() {
		return loggers.values();
	}

	private Logger(String logName) {
		super(JAC.getInstance(), logName, LoggerConfig.isNewFile());
		setPrintToConsole(LoggerConfig.isConsoleLogging());
		setWriteToLog(LoggerConfig.isFileLogging());
		loggers.put(logName, this);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHackEvent(HackEvent event) {
		general.log(event.getPlayer().getName() + " " + event.getType().getActionDescription() + " "
				+ event.getExtraData());
		if (event.isCancelled())
			general.log("The hack event was cancelled by a plugin");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAutoban(AutobanEvent event) {
		String timeString = event.isPerm() ? "permanently " : "for " + event.getDays() + " days";
		general.log(event.getPlayer().getName() + " was autobanned " + timeString + "for " + event.getType().getName());
		if (!event.getExtraData().equals(""))
			general.log("Extra data:\t"+event.getExtraData());
		if (event.isCancelled())
			general.log("The autoban event was cancelled by a plugin");
	}
}
