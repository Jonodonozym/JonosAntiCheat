
package jdz.jac.punisher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.events.Listener;
import jdz.jac.JAC;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;

public class Punisher implements Listener {
	private final Map<HackType, Map<Player, Double>> percent = new HashMap<HackType, Map<Player, Double>>();
	private final Set<Player> autobanned = new HashSet<Player>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHackEvent(HackEvent event) {
		Player player = event.getPlayer();
		HackType type = event.getType();

		if (autobanned.contains(player))
			return;

		if (!percent.containsKey(type))
			percent.put(type, new HashMap<Player, Double>());

		if (!percent.get(type).containsKey(player))
			percent.get(type).put(player, 0D);

		double change = (double) (type.getSeverity().ordinal()) / (double) Severity.values().length
				* event.getSeverityModifier();

		percent.get(type).put(player, percent.get(type).get(player) + change);

		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			if (percent.get(type).containsKey(player))
				percent.get(type).put(player, percent.get(type).get(player) - change);
		}, (long) (type.getPersistanceTicks() * event.getPersistanceTicksModifier()));

		if (percent.get(type).get(player) >= 1)
			autoban(event.getPlayer(), event.getType());
	}

	private void autoban(Player player, HackType type) {
		if (!PunisherConfig.autobanEnabled(type))
			return;
		
		BanLevel level = PunisherConfig.getBanLevel(type);

		if (level.isTemp()) {
			int previousBans = HistoryFetcher.getBans(player, level);
			int maxBans = PunisherConfig.getBansTilPerm(level);
			if (previousBans > maxBans)
				level = BanLevel.PERMANENT;
		}

		int unbanTier = PunisherConfig.getUnbanTier(level);
		int days = PunisherConfig.getTempBanDays(level);
		boolean isPerm = level == BanLevel.PERMANENT || level == BanLevel.UNIQUE;

		AutobanEvent event = new AutobanEvent(player, type, unbanTier, days, isPerm);
		event.call();

		if (event.isCancelled())
			return;

		if (PunisherConfig.isKickInstead()) {
			player.kickPlayer(
					ChatColor.RED + "A connection error occurred. Contact the server owners if this issue persists");
			return;
		}

		if (!PunisherConfig.isRunCommands())
			return;

		autobanned.add(player);
		if (isPerm) {
			String info = player.getName() + " " + type.getName() + " [Unban Tier #" + event.getUnbanTier() + "] -s";
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban " + info);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "banip " + info);
		}
		else {
			String info = player.getName() + " " + days + "d" + type.getName() + " [Unban Tier #" + event.getUnbanTier()
					+ "]";
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempban " + info);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tempbanip " + info);
		}

	}
}
