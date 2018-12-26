
package jdz.jac;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import jdz.jac.detection.aimbot.AimbotConfig;
import jdz.jac.detection.aimbot.AimbotDetector;
import jdz.jac.detection.aimbot.command.AimbotCommandExecutor;
import jdz.jac.detection.autoarmor.AutoArmorCheckCommand;
import jdz.jac.detection.autoarmor.AutoArmorConfig;
import jdz.jac.detection.autoarmor.AutoArmorDetector;
import jdz.jac.detection.civbreak.CivBreakConfig;
import jdz.jac.detection.civbreak.CivBreakDetector;
import jdz.jac.detection.wallhack.WallhackConfig;
import jdz.jac.detection.wallhack.WallhackDetector;
import jdz.jac.logger.LoggerConfig;
import jdz.jac.logger.LoggerListener;
import jdz.jac.notifier.Notifier;
import jdz.jac.notifier.NotifierConfig;
import jdz.jac.ping.PingCommand;
import jdz.jac.ping.PingTop;
import jdz.jac.ping.PingTopCommand;
import jdz.jac.ping.compensation.PlayerLocationHistory;
import jdz.jac.punisher.Punisher;
import jdz.jac.punisher.PunisherConfig;
import lombok.Getter;

public class JAC extends JavaPlugin {
	@Getter private static JAC instance;
	private static JACCommandExecutor commands;

	@Override
	public void onEnable() {
		instance = this;

		new LoggerListener().registerEvents(this);
		new LoggerConfig(this).register();

		new Notifier().registerEvents(this);
		new NotifierConfig(this).register();

		new Punisher().registerEvents(this);
		new PunisherConfig(this).registerEvents(this);
		
		PlayerLocationHistory.init(this);
		
		PingTop.startUpdater();
		new PingCommand().register(this);
		new PingTopCommand().register(this);

		commands = new JACCommandExecutor(this);
		commands.register();

		new AutoArmorDetector().registerEvents(this);
		new AutoArmorConfig(this).register();
		commands.add(new AutoArmorCheckCommand());

		new CivBreakDetector().registerEvents(this);
		new CivBreakConfig(this).register();

		new WallhackDetector().registerEvents(this);
		new WallhackConfig(this).register();

		new AimbotDetector(this).registerEvents(this);
		new AimbotConfig(this).register();
		commands.add(new AimbotCommandExecutor(commands));
	}

	public File getStorageFolder() {
		File serverRoot = getDataFolder().getAbsoluteFile().getParentFile().getParentFile();
		File serverList = serverRoot.getParentFile();
		File storageFolder = new File(serverList, "JAC" + File.separator + getServer().getName());
		if (!storageFolder.exists())
			storageFolder.mkdirs();
		return storageFolder;
	}
}