
package jdz.jac.detection;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

import jdz.bukkitUtils.components.events.Listener;
import jdz.bukkitUtils.configuration.Config;
import jdz.bukkitUtils.configuration.ConfigReloadEvent;
import jdz.jac.JAC;

public class TypeEnabledConfig implements Listener {
	private static FileConfiguration config = Config.getConfig(JAC.getInstance());

	public static boolean isEnabled(HackType type) {
		return config.getBoolean(configKey(type), true);
	}

	private static String configKey(HackType type) {
		return type.getName() + ".enabled";
	}

	@EventHandler
	public void configReload(ConfigReloadEvent event) {
		if (event.getPlugin().equals(JAC.getInstance()))
			config = event.getConfig();
		addDefaultSections(config, Config.getConfigFile(JAC.getInstance(), event.getName()));
	}

	private static void addDefaultSections(FileConfiguration config, File file) {
		for (HackType type : HackType.getAllTypes())
			if (!config.contains(configKey(type)))
				config.set(configKey(type), false);
		try {
			config.save(file);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
