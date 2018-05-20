
package jdz.jac.punisher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.events.Listener;
import jdz.jac.JAC;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;

public class Punisher implements Listener {
	private Map<HackType, Map<Player, Double>> percent = new HashMap<HackType, Map<Player, Double>>();
	private Set<Player> autobanPending = new HashSet<Player>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHackEvent(HackEvent event) {
		Player player = event.getPlayer();
		HackType type = event.getType();

		if (autobanPending.contains(player))
			return;

		if (!percent.containsKey(type))
			percent.put(type, new HashMap<Player, Double>());

		if (!percent.get(type).containsKey(player))
			percent.get(type).put(player, 0D);

		double change = (type.getSeverity().ordinal()) / Severity.values().length * event.getSeverityModifier();
		percent.get(type).put(player, percent.get(type).get(player) + change);
		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			if (percent.get(type).containsKey(player))
				percent.get(type).put(player, percent.get(type).get(player) - change);
		}, (long) (type.getPersistanceTicks() * event.getPersistanceTicksModifier()));
		
		if (percent.get(type).get(player) >= 1)
			autoban(event.getPlayer(), event.getType());
	}
	
	private void autoban(Player player, HackType type) {
		// TODO
	}
}
