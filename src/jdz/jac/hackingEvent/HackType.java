package jdz.jac.hackingEvent;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.misc.Config;
import jdz.jac.JAC;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Wither;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class HackType {
	public static final HackType WALLHACK_OPEN_CONTAINER = new HackType("WallhackContainer",
			"Tried to open a container through a wall", Severity.HIGH, 50);
	public static final HackType WALLHACK_BREAK_BLOCK = new HackType("WallhackBreak",
			"Tried to break a block through a wall", Severity.HIGH, 80);
	public static final HackType WALLHACK_HIT_PLAYER = new HackType("WallhackHit",
			"Tried to hit a player through a wall", Severity.HIGH, 200); // TODO implement this
	public static final HackType CIV_BREAK = new HackType("CivBreak", "Broke the nexus too fast", Severity.HIGH, 100);

	private static final FileConfiguration enabledConfig = Config.getConfig(JAC.getInstance());
	
	private final String name;
	private final String actionDescription;
	private final Severity severity;
	private final int persistanceTicks;
	@Wither private EventPriority priority = EventPriority.MONITOR;

	public boolean isEnabled() {
		return enabledConfig.getBoolean(name + ".enabled", true);
	}
}
