
package jdz.jac.detection.autoarmor;

import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.config.AutoConfig;
import lombok.Getter;

public class AutoArmorConfig extends AutoConfig {
	@Getter private static int autoequipTicks = 5;
	@Getter private static boolean addPingToTicks = true;

	public AutoArmorConfig(Plugin plugin) {
		super(plugin, "AutoArmor");
	}
}