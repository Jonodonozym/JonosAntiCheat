
package jdz.jac.detection.superspeed;

import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.config.AutoConfig;
import lombok.Getter;

public class SuperSpeedConfig extends AutoConfig {
	@Getter private static double maxSpeed = 24;
	@Getter private static int ticksThreshold = 5;

	public SuperSpeedConfig(Plugin plugin) {
		super(plugin, "superSpeed");
	}
}
