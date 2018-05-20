
package jdz.jac.notifier;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import jdz.bukkitUtils.events.Listener;
import jdz.jac.JAC;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;

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
		}, NotifierConfig.getNotifyCooldownTicks());

		broadcastNotification(player, type);
	}

	public static void broadcastNotification(Player cheater, HackType type) {
		for (Player notifier : Bukkit.getOnlinePlayers())
			if (shouldNotify(notifier, cheater))
				sendNotification(notifier, cheater, type);
	}

	public static boolean shouldNotify(Player player, Player cheater) {
		if (player.isOp())
			return NotifierConfig.isNotifyOP();
		if (player.equals(cheater))
			return false;
		for (String permission : NotifierConfig.getNotifyPermissions())
			if (player.hasPermission(permission))
				return true;
		return false;
	}

	public static void sendNotification(Player player, Player cheater, HackType type) {
		player.sendMessage(RED + "[" + BOLD + GOLD + "JAC" + RESET + RED + "]" + GOLD + " " + cheater.getName() + " "
				+ type.getActionDescription());
		if (type.getComponent() != null)
			player.spigot().sendMessage(type.getComponent());
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
	}
}
