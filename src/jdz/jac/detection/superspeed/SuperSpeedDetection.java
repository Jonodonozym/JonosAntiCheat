
package jdz.jac.detection.superspeed;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.misc.VelocityTracker;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;
import static jdz.jac.detection.superspeed.SuperSpeedConfig.*;

public class SuperSpeedDetection {
	private static final HackType superSpeedHackType = new HackType("Super Speed", "Is moving way too fast",
			Severity.HIGH, 60);

	private static final Map<Player, Integer> strikes = new HashMap<>();

	public static void init(Plugin plugin) {
		VelocityTracker.init();

		Bukkit.getScheduler().runTaskTimer(plugin, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (isMovingTooFast(player))
					addStrike(player);
				else
					removeStrike(player);
				checkStrikesUnderThreshold(player);
			}
		}, 1, 1);
	}

	private static boolean isMovingTooFast(Player player) {
		return VelocityTracker.getVelocity(player).setY(0).lengthSquared() > getMaxSpeed() * getMaxSpeed();
	}

	private static void addStrike(Player player) {
		if (!strikes.containsKey(player))
			strikes.put(player, 0);
		strikes.put(player, strikes.get(player) + 1);
	}

	private static void removeStrike(Player player) {
		if (strikes.containsKey(player) && strikes.get(player) > 0)
			strikes.put(player, strikes.get(player) - 1);
	}

	private static void checkStrikesUnderThreshold(Player player) {
		if (strikes.get(player) >= getTicksThreshold()) {
			double speed = VelocityTracker.getVelocity(player).setY(0).length();
			new HackEvent(player, superSpeedHackType, "Speed: " + speed).call();
			strikes.put(player, 0);
		}
	}
}