
package jdz.jac.detection.autoarmor;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;
import lombok.AccessLevel;
import lombok.Getter;

public class AutoArmorConfig implements Listener {
	@Getter(value = AccessLevel.PACKAGE) private boolean enabled = true;
	
	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(JAC.getInstance()))
			return;

		FileConfiguration config = event.getConfig();
		enabled = config.getBoolean("autoarmor.enabled", true);
	}

}
