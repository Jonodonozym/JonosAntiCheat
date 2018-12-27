
package jdz.jac.ping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.misc.Pair;
import jdz.jac.JAC;

public class PingTop {
	private static List<Pair<Player, Integer>> top = new ArrayList<>();
	private static Map<Player, Integer> positions = new HashMap<>();

	public static void startUpdater() {
		Bukkit.getScheduler().runTaskTimerAsynchronously(JAC.getInstance(), () -> {
			updateTop();
			updatePositions();
		}, 20, 20 * 30);
	}

	private static void updateTop() {
		top.clear();
		for (Player player : Bukkit.getOnlinePlayers())
			top.add(new Pair<>(player, PingFetcher.getPing(player)));
		top.sort((a, b) -> {
			return a.getValue() < b.getValue() ? -1 : 1;
		});
	}

	private static void updatePositions() {
		positions.clear();
		int i = 0;
		for (Pair<Player, Integer> entry : top)
			positions.put(entry.getKey(), ++i);
	}

	public static int size() {
		return top.size();
	}

	public static Pair<Player, Integer> getByRank(int rank) {
		return top.get(rank);
	}

	public static int getPosition(Player player) {
		if (!positions.containsKey(player))
			return -1;
		return positions.get(player);
	}


}
