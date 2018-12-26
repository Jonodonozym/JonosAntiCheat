
package jdz.jac.detection;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.bukkitUtils.misc.Config;
import jdz.jac.JAC;

public class TypeEnabledConfig implements Listener {
	private static FileConfiguration config = Config.getConfig(JAC.getInstance());

	@EventHandler
	public void configReload(ConfigReloadEvent event) {
		if (event.getPlugin().equals(JAC.getInstance()))
			config = event.getConfig();
	}

	public static boolean isEnabled(HackType type) {
		return config.getBoolean(type.getName() + ".enabled", true);
	}
}
