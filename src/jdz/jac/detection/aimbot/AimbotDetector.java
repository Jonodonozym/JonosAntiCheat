
package jdz.jac.detection.aimbot;

import static jdz.jac.detection.aimbot.AimbotConfig.allowedClasses;
import static jdz.jac.detection.aimbot.AimbotConfig.minRecheckIntervalSeconds;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.CombatEnterEvent;
import jdz.bukkitUtils.misc.CombatTimer;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;
import jdz.jac.detection.aimbot.aimData.DataRecorder;

public class AimbotDetector implements Listener {
	private static final HackType AIMBOT_HACKTYPE = new HackType("aimbot",
			"aim is consistent with measured aimbot data", Severity.NONE, 600);

	private final CombatTimer timer;
	private final Map<Player, Long> lastChecks = new HashMap<>();

	public AimbotDetector(Plugin plugin) {
		timer = new CombatTimer(plugin, 200);
		timer.setDoMessages(false);
		timer.setTriggeredByMobs(true);
		DataRecorder.register(plugin, timer);
	}

	@EventHandler
	public void onCombatEnter(CombatEnterEvent event) {
		if (!event.getTimer().equals(timer))
			return;

		if (!shouldCheck(event.getPlayer()))
			return;

		AimbotClassifier.classify(event.getPlayer(), (result) -> {
			if (allowedClasses.contains(result.getBestMatched()))
				return;

			new HackEvent(event.getPlayer(), AIMBOT_HACKTYPE,
					"\n\t type: " + result.getBestMatched() + "\n\t dist: " + result.getDistance()).call();
		}, () -> {}, 15);
	}

	private boolean shouldCheck(Player player) {
		if (!lastChecks.containsKey(player))
			return true;
		return (System.currentTimeMillis() - lastChecks.get(player)) * 1000 < minRecheckIntervalSeconds;
	}

}
