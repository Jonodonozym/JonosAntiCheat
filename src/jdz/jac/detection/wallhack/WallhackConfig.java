
package jdz.jac.detection.wallhack;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;

public class WallhackConfig implements Listener {
	public static final HashSet<Material> transparentMaterials = new HashSet<Material>();
	public static final Set<Material> containersToIgnore = new HashSet<Material>();

	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(JAC.getInstance()))
			return;

		FileConfiguration config = event.getConfig();

		transparentMaterials.clear();
		for (String s : config.getStringList("wallhack.canClickThrough"))
			transparentMaterials.add(parseMaterial(s));

		containersToIgnore.clear();
		for (String s : config.getStringList("wallhack.containersToIgnore"))
			containersToIgnore.add(parseMaterial(s));
	}


	@SuppressWarnings("deprecation")
	private static Material parseMaterial(String material) {
		try {
			return Material.matchMaterial(material);
		}
		catch (Exception e) {
			try {
				return Material.getMaterial(Integer.parseInt(material));
			}
			catch (Exception e2) {
				System.out.println("Could not parse block to ignore: " + material);
			}
		}
		return null;
	}
}
