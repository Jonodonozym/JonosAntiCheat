
package jdz.jac;

import org.bukkit.plugin.java.JavaPlugin;

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
import jdz.jac.punisher.Punisher;
import jdz.jac.punisher.PunisherConfig;
import lombok.Getter;

public class JAC extends JavaPlugin {
	@Getter private static JAC instance;

	@Override
	public void onEnable() {
		instance = this;
		
		new LoggerConfig().registerEvents(this);
		new LoggerListener().registerEvents(this);
		
		new AutoArmorCheckCommand().register(this);
		new AutoArmorConfig().registerEvents(this);
		new AutoArmorDetector().registerEvents(this);

		new CivBreakDetector().registerEvents(this);
		new CivBreakConfig().registerEvents(this);
		
		new WallhackConfig().registerEvents(this);
		new WallhackDetector().registerEvents(this);
		
		new Notifier().registerEvents(this);
		new NotifierConfig().registerEvents(this);
		
		new Punisher().registerEvents(this);
		new PunisherConfig().registerEvents(this);
	}
}