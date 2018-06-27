
package jdz.jac.detection.civbreak;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;
import lombok.Getter;

public class CivBreakConfig implements Listener {
	@Getter private static boolean enabled = true;

	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(JAC.getInstance()))
			return;

		FileConfiguration config = event.getConfig();

		enabled = config.getBoolean("civbreak.enabled", true);
	}
}
