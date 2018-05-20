
package jdz.jac.notifier;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;
import lombok.AccessLevel;
import lombok.Getter;

public class NotifierConfig implements Listener{
	@Getter(value = AccessLevel.PACKAGE) private static boolean notifyOP = true;
	@Getter(value = AccessLevel.PACKAGE) private static List<String> notifyPermissions = new ArrayList<String>();
	@Getter(value = AccessLevel.PACKAGE) private static int notifyCooldownTicks = 1;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(JAC.getInstance()))
			return;

		FileConfiguration config = event.getConfig();

		notifyOP = config.getBoolean("notification.opEnabled");

		if (config.getBoolean("notification.permissionsEnabled"))
			notifyPermissions = config.getStringList("notification.permissions");
		notifyCooldownTicks = config.getInt("notification.cooldownSeconds") * 20;
	}
}
