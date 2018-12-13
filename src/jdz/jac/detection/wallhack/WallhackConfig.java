
package jdz.jac.detection.wallhack;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.config.AutoConfig;
import lombok.Getter;

public class WallhackConfig extends AutoConfig {
	@Getter private static final Set<Material> canClickThrough = new HashSet<Material>();
	@Getter private static final Set<Material> containersToIgnore = new HashSet<Material>();

	public WallhackConfig(Plugin plugin) {
		super(plugin, "wallhack");
	}
}
