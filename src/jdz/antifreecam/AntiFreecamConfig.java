
package jdz.antifreecam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import jdz.bukkitUtils.misc.Config;

public class AntiFreecamConfig {
	public static final HashSet<Material> canClickThrough = new HashSet<Material>();
	public static final Set<Material> containersToIgnore = new HashSet<Material>();

	public static int notifyCooldownTicks = 1;
	public static boolean notifyOP = false;
	public static boolean fileLogging = true;
	public static boolean consoleLogging = false;
	public static List<String> notifyPermission = new ArrayList<String>();
	
	public static void reload(AntiFreecamPlugin plugin) {
		for (Material m : Material.values())
			if (!m.isOccluding() || m.isTransparent())
				canClickThrough.add(m);

		FileConfiguration config = Config.getConfig(plugin);
		notifyOP = config.getBoolean("notification.opEnabled");
		fileLogging = config.getBoolean("notification.fileLogging");
		consoleLogging = config.getBoolean("notification.consoleLogging");
		if (config.getBoolean("notification.permissionsEnabled"))
			notifyPermission = config.getStringList("permissions");
		notifyCooldownTicks = config.getInt("notification.cooldownSeconds") * 20;

		for (String s : config.getStringList("canClickThrough")) 
			canClickThrough.add(parseMaterial(s, plugin));
		
		for (String s : config.getStringList("containersToIgnore"))
			containersToIgnore.add(parseMaterial(s, plugin));
	}

	@SuppressWarnings("deprecation")
	private static Material parseMaterial(String material, AntiFreecamPlugin plugin) {
		try {
			return Material.matchMaterial(material);
		}
		catch (Exception e) {
			try {
				return Material.getMaterial(Integer.parseInt(material));
			}
			catch (Exception e2) {
				plugin.getLogger().warning("Could not parse block to ignore: " + material);
			}
		}
		return null;
	}
}
