
package jdz.jac.logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.jac.JAC;
import lombok.Getter;

public class Logger extends FileLogger {
	private static final Map<String, Logger> loggers = new HashMap<>();
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
		super(JAC.getInstance(), logName, !LoggerConfig.isSingleFile());
		setPrintToConsole(LoggerConfig.isConsole());
		setWriteToLog(LoggerConfig.isFile());
		loggers.put(logName, this);
	}

	@Override
	public String getLogDirectory() {
		return JAC.getInstance().getStorageFolder().getAbsolutePath();
	}
}
