
package jdz.jac;

import org.bukkit.plugin.java.JavaPlugin;

import jdz.jac.detection.civbreak.CivBreakConfig;
import jdz.jac.detection.civbreak.CivBreakListener;
import jdz.jac.detection.wallhack.WallhackConfig;
import jdz.jac.detection.wallhack.WallhackListener;
import jdz.jac.punisher.Punisher;
import jdz.jac.util.Notifier;
import lombok.Getter;

public class JAC extends JavaPlugin {
	@Getter private static JAC instance;

	@Override
	public void onEnable() {
		instance = this;

		new CivBreakListener().registerEvents(this);
		new CivBreakConfig().registerEvents(this);
		
		new WallhackConfig().registerEvents(this);
		new WallhackListener().registerEvents(this);
		
		new Notifier().registerEvents(this);
		
		new Punisher().registerEvents(this);
	}
}