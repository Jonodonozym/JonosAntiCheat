
package jdz.jac.detection.aimbot;

import static jdz.jac.detection.aimbot.AimbotConfig.isClassAllowed;
import static jdz.jac.detection.aimbot.AimbotConfig.minRecheckIntervalSeconds;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import static org.bukkit.ChatColor.*;
import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.CombatEnterEvent;
import jdz.bukkitUtils.misc.CombatTimer;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;
import jdz.jac.detection.aimbot.aimData.DataManager;
import jdz.jac.detection.aimbot.aimData.DataRecorder;

public class AimbotDetector implements Listener {
	private static final HackType AIMBOT_HACKTYPE = new HackType("aimbot", "%player%'s aim is consistent with aimbot data",
			Severity.NONE, 600);

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

		lastChecks.put(event.getPlayer(), System.currentTimeMillis());

		System.out.println("Autochecking " + event.getPlayer().getName());
		try {
			AimbotClassifier.classify(event.getPlayer(), (data, result) -> {
				if (isClassAllowed(result.getBestMatched()))
					return;

				new HackEvent(event.getPlayer(), AIMBOT_HACKTYPE, Arrays.toString(data),
						GREEN + "type: " + YELLOW + result.getBestMatched(),
						GREEN + "dist: " + YELLOW + result.getDistance()).call();
			}, 15);
		}
		catch (IllegalStateException e) {}
	}

	private boolean shouldCheck(Player player) {
		if (DataManager.isRecording(player))
			return false;
		if (!lastChecks.containsKey(player))
			return true;
		return (System.currentTimeMillis() - lastChecks.get(player)) * 1000 > minRecheckIntervalSeconds;
	}

}
