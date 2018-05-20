package jdz.jac.detection;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.misc.Config;
import jdz.jac.JAC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.TextComponent;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class HackType {
	private static final FileConfiguration enabledConfig = Config.getConfig(JAC.getInstance());
	
	private final String name;
	private final String actionDescription;
	private final Severity severity;
	private final int persistanceTicks;
	private EventPriority priority = EventPriority.MONITOR;
	private TextComponent component = null;

	public boolean isEnabled() {
		return enabledConfig.getBoolean(name + ".enabled", true);
	}
}
