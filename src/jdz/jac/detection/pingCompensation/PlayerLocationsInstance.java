
package jdz.jac.detection.pingCompensation;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode(of = { "timestamp" })
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerLocationsInstance {
	private final Map<Player, Location> locations;
	private final Map<Player, Location> eyeLocations;
	@Getter private final long timestamp;

	public Location getLocation(Player player) {
		return locations.get(player);
	}

	public Location getEyeLocation(Player player) {
		return eyeLocations.get(player);
	}

	public boolean contains(Player player) {
		return locations.containsKey(player);
	}

	public static PlayerLocationsInstance getCurrent() {
		Map<Player, Location> locs = new HashMap<>();
		Map<Player, Location> eyeLocs = new HashMap<>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			locs.put(player, player.getLocation());
			locs.put(player, player.getEyeLocation());
		}
		return new PlayerLocationsInstance(locs, eyeLocs, System.currentTimeMillis());
	}
}
