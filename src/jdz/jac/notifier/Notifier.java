
package jdz.jac.notifier;

import static org.bukkit.ChatColor.*;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.events.Listener;
import jdz.jac.JAC;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.punisher.AutobanEvent;

public class Notifier implements Listener {
	private static final Set<Player> cooldownHackers = new HashSet<Player>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHackEvent(HackEvent event) {
		Player player = event.getPlayer();
		HackType type = event.getType();

		if (cooldownHackers.contains(player))
			return;
		cooldownHackers.add(player);
		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			cooldownHackers.remove(player);
		}, NotifierConfig.getCooldownSeconds() * 20);

		broadcastNotification(player, type, event.getExtraData(), event.getExtraData());
	}

	public static void broadcastNotification(Player cheater, HackType type, String extraData, String loggerData) {
		for (Player notifier : Bukkit.getOnlinePlayers())
			if (shouldNotify(notifier, cheater))
				notify(notifier, cheater, type, extraData);
		if (NotifierConfig.isConsoleEnabled())
			notify(Bukkit.getConsoleSender(), cheater, type, loggerData);
	}

	public static boolean shouldNotify(Player player, Player cheater) {
		if (player.isOp())
			return NotifierConfig.isOpEnabled();
		if (player.equals(cheater))
			return false;
		for (String permission : NotifierConfig.getPermissions())
			if (player.hasPermission(permission))
				return true;
		return false;
	}

	public static void notify(CommandSender toNotify, Player cheater, HackType type, String extraData) {
		toNotify.sendMessage(format(cheater.getName() + " " + type.getActionDescription() + " " + extraData));
		if (toNotify instanceof Player) {
			Player player = (Player) toNotify;
			player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1f, 2f);
		}
	}

	private static String[] format(String message) {
		String[] lines = message.split("\n");
		lines[0] = RED + "[" + BOLD + GOLD + "JAC" + RESET + RED + "]" + GOLD + " " + lines[0];
		for (int i = 1; i < lines.length; i++)
			lines[i] = GOLD + lines[i];
		return lines;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAutobanEvent(AutobanEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!(NotifierConfig.isBroadcastAutobans()
					|| NotifierConfig.isBroadcastAutobansAdmin() && shouldNotify(player, event.getPlayer())))
				continue;
			String timeString = event.isPerm() ? "permanently " : "for " + event.getDays() + " days";
			player.sendMessage(format(event.getPlayer().getName() + " got autobanned " + timeString + "for "
					+ event.getType().getName()));
			player.sendMessage(format(NotifierConfig.getRandomAutobanGloatMessage()));
		}
	}
}
