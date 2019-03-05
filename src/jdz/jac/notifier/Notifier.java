
package jdz.jac.notifier;

import static jdz.jac.notifier.Messager.message;

import java.lang.reflect.Field;
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
import jdz.jac.punisher.AutobanEvent;

public class Notifier implements Listener {
	private static final Set<Player> cooldownHackers = new HashSet<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onHackEvent(HackEvent event) {
		Player player = event.getPlayer();

		if (cooldownHackers.contains(player))
			return;
		cooldownHackers.add(player);
		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			cooldownHackers.remove(player);
		}, NotifierConfig.getCooldownSeconds() * 20);

		broadcastNotification(player, event);
	}

	public static void broadcastNotification(Player cheater, HackEvent event) {
		for (Player notifier : Bukkit.getOnlinePlayers())
			if (shouldNotify(notifier, cheater))
				notify(notifier, cheater, event);
		if (NotifierConfig.isConsoleEnabled())
			notify(Bukkit.getConsoleSender(), cheater, event);
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

	public static void notify(CommandSender toNotify, Player cheater, HackEvent event) {
		message(toNotify, event.getActionDescription() + event.getExtraData());
		if (toNotify instanceof Player) {
			Player player = (Player) toNotify;
			player.playSound(player.getLocation(), notifySound, 1f, 2f);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onAutobanEvent(AutobanEvent event) {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!(NotifierConfig.isBroadcastAutobans()
					|| NotifierConfig.isBroadcastAutobansAdmin() && shouldNotify(player, event.getPlayer())))
				continue;
			String timeString = event.isPerm() ? "permanently " : "for " + event.getDays() + " days";
			message(player,
					event.getPlayer().getName() + " got autobanned " + timeString + "for " + event.getType().getName());
			message(player, NotifierConfig.getRandomAutobanGloatMessage());
		}
	}

	private static final Sound notifySound;
	static {
		Sound s = null;
		for (Field field : Sound.class.getDeclaredFields())
			if (field.getName().endsWith("ORB_PICKUP")) {
				try {
					s = (Sound) field.get(null);
				}
				catch (ReflectiveOperationException e) {
					e.printStackTrace();
				}
				break;
			}
		notifySound = s;
	}
}
