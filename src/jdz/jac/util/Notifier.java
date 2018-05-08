
package jdz.jac.util;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;
import jdz.jac.hackingEvent.HackEvent;
import jdz.jac.hackingEvent.HackType;

public class Notifier implements Listener {
	private static final Set<Player> cooldownHackers = new HashSet<Player>();

	private static boolean notifyOP = true;
	private static List<String> notifyPermissions = new ArrayList<String>();
	private static int notifyCooldownTicks = 1;

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onConfigReload(ConfigReloadEvent event) {
		if (!event.getPlugin().equals(JAC.getInstance()))
			return;

		FileConfiguration config = event.getConfig();

		notifyOP = config.getBoolean("notification.opEnabled");

		if (config.getBoolean("notification.permissionsEnabled"))
			notifyPermissions = config.getStringList("notification.permissions");
		notifyCooldownTicks = config.getInt("notification.cooldownSeconds") * 20;
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHackEvent(HackEvent event) {
		Player player = event.getPlayer();
		HackType type = event.getType();

		if (cooldownHackers.contains(player))
			return;
		cooldownHackers.add(player);
		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			cooldownHackers.remove(player);
		}, notifyCooldownTicks);

		broadcastNotification(player, type);
	}

	public static void broadcastNotification(Player cheater, HackType type) {
		for (Player notifier : Bukkit.getOnlinePlayers())
			if (shouldNotify(notifier, cheater))
				sendNotification(notifier, cheater, type);
	}

	public static boolean shouldNotify(Player player, Player cheater) {
		if (player.isOp())
			return notifyOP;
		if (player.equals(cheater))
			return false;
		for (String permission : notifyPermissions)
			if (player.hasPermission(permission))
				return true;
		return false;
	}

	public static void sendNotification(Player player, Player cheater, HackType type) {
		player.sendMessage(RED + "[" + BOLD + GOLD + "JAC" + RESET + RED + "]" + GOLD + " " + cheater.getName() + " "
				+ type.getActionDescription());
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
	}
}
