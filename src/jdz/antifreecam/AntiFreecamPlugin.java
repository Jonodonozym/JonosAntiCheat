
package jdz.antifreecam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;

import static org.bukkit.ChatColor.*;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.Config;
import jdz.bukkitUtils.misc.WorldUtils;

public class AntiFreecamPlugin extends JavaPlugin implements Listener {
	private final FileLogger logger = new FileLogger(this);

	private boolean notifyOP = false;
	private boolean doLogging = true;
	private List<String> notifyPermission = new ArrayList<String>();

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		FileConfiguration config = Config.getConfig(this);
		notifyOP = config.getBoolean("notification.opEnabled");
		doLogging = config.getBoolean("doLogging");
		if (config.getBoolean("notification.permissionsEnabled"))
			notifyPermission = config.getStringList("permissions");
	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		InventoryHolder holder = e.getInventory().getHolder();
		if (!(holder instanceof BlockState))
			return;

		Player player = (Player) e.getPlayer();
		Block block = ((BlockState) holder).getBlock();
		Block target = getTargetBlock(player);

		System.out.println(player.getLocation());
		System.out.println(target.getLocation());
		System.out.println(block.getLocation());

		if (!block.getLocation().equals(target.getLocation())) {
			e.setCancelled(true);
			if (doLogging)
				log(player, block, target);

			for (Player p : Bukkit.getOnlinePlayers())
				if (shouldNotify(p, player))
					sendNotification(p, player);
		}
	}

	private Block getTargetBlock(Player player) {
		return player.getTargetBlock((HashSet<Byte>)null, 8);
	}

	private boolean shouldNotify(Player player, Player cheater) {
		if (player.isOp())
			return notifyOP;
		if (player.equals(cheater))
			return false;
		for (String permission : notifyPermission)
			if (player.hasPermission(permission))
				return true;
		return false;
	}
	
	private void log(Player player, Block block, Block target) {
		getLogger().info(
				player.getName() + " Tried to open a chest through a wall! Check AntiFreecam logs for details");
		logger.log(player.getName() + " Tried to open a container through a wall!\n" + "\tContainer location: "
				+ WorldUtils.locationToLegibleString(block.getLocation()) + "\n" + "\tPlayer location: "
				+ WorldUtils.locationToLegibleString(player.getLocation()) + "\n" + "\tPlayer target block: "
				+ WorldUtils.locationToLegibleString(target.getLocation()) + "\n" + "\tTarget Block Type: "
				+ target.getType().name());
	}

	private void sendNotification(Player player, Player cheater) {
		player.sendMessage(RED + "[" + BOLD + GOLD + "AntiFreecam" + RESET + RED + "]" + GOLD + " " + cheater.getName()
				+ " Tried to open a chest through a wall!");
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
	}
}