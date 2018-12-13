
package jdz.jac.detection.autoarmor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.config.AutoConfig;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;
import lombok.Getter;

public class AutoArmorConfig extends AutoConfig {
	@Getter private static boolean enabled = true;
	@Getter private static int autoequipTicks = 5;
	@Getter private static boolean addPingToTicks = true;

	public AutoArmorConfig(Plugin plugin) {
		super(plugin, "autoarmor");
	}
	
	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(JAC.getInstance()))
			return;

		FileConfiguration config = event.getConfig();
		enabled = config.getBoolean("autoarmor.enabled", true);
		autoequipTicks = config.getInt("autoarmor.autoequipTicks", 5);
		addPingToTicks = config.getBoolean("autoarmor.addPingToTicks", true);
	}

}
