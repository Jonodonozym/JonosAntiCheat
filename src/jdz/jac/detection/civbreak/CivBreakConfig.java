
package jdz.jac.detection.civbreak;

import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.config.AutoConfig;
import lombok.Getter;

public class CivBreakConfig extends AutoConfig {
	@Getter private static boolean enabled = true;

	public CivBreakConfig(Plugin plugin) {
		super(plugin, "civbreak");
	}
}
