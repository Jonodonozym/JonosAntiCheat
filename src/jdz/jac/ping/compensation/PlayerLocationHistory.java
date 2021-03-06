
package jdz.jac.ping.compensation;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import jdz.jac.ping.PingFetcher;

public class PlayerLocationHistory {
	private static List<PlayerLocationsInstance> history = new ArrayList<>();

	public static void init(Plugin plugin) {
		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			history.add(0, PlayerLocationsInstance.getCurrent());
			if (history.size() > 20)
				history.remove(history.size() - 1);
		}, 0, 1);
	}

	public static Location getLocationFromPerspective(Player target, Player player) {
		return getInstanceOfFromPerspective(target, player).getLocation(target);
	}

	public static Location getEyeLocationFromPerspective(Player target, Player player) {
		return getInstanceOfFromPerspective(target, player).getEyeLocation(target);
	}

	public static PlayerLocationsInstance getInstanceOfFromPerspective(Player target, Player player) {
		int ping = PingFetcher.getPing(player);
		return getEarliestInstanceContaining(target, ping / 50);
	}

	private static PlayerLocationsInstance getEarliestInstanceContaining(Player player, int maxTicksAgo) {
		if (maxTicksAgo > history.size() - 1)
			maxTicksAgo = history.size();

		PlayerLocationsInstance instance = history.get(maxTicksAgo);
		while (!instance.contains(player)) {
			instance = history.get(--maxTicksAgo);
			if (maxTicksAgo == 0)
				return instance;
		}
		return instance;
	}
}
