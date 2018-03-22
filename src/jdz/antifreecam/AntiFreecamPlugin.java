
package jdz.antifreecam;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;

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
	private boolean fileLogging = true;
	private boolean consoleLogging = false;
	private List<String> notifyPermission = new ArrayList<String>();
	private final HashSet<Material> transparentBlocks = new HashSet<Material>();

	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);

		for (Material m : Material.values())
			if (m.isTransparent())
				transparentBlocks.add(m);
		transparentBlocks.add(Material.REDSTONE_WIRE);


		FileConfiguration config = Config.getConfig(this);
		notifyOP = config.getBoolean("notification.opEnabled");
		fileLogging = config.getBoolean("notification.fileLogging");
		consoleLogging = config.getBoolean("notification.consoleLogging");
		if (config.getBoolean("notification.permissionsEnabled"))
			notifyPermission = config.getStringList("permissions");


		for (String s : config.getStringList("ignoreBlocks")) {
			try {
				transparentBlocks.add(Material.matchMaterial(s));
			}
			catch (Exception e) {
				try {
					transparentBlocks.add(Material.getMaterial(Integer.parseInt(s)));
				}
				catch (Exception e2) {
					getLogger().warning("could not parse block to ignore: "+s);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		InventoryHolder holder = e.getInventory().getHolder();
		if (!(holder instanceof BlockState))
			return;

		Player player = (Player) e.getPlayer();
		Block block = ((BlockState) holder).getBlock();
		Block target = getTargetBlock(player);

		if (!block.getLocation().equals(target.getLocation())) {
			e.setCancelled(true);
			if (fileLogging)
				log(player, block, target);

			if (consoleLogging)
				getLogger().info(
						player.getName() + " Tried to open a chest through a wall! Check AntiFreecam logs for details");

			for (Player p : Bukkit.getOnlinePlayers())
				if (shouldNotify(p, player))
					sendNotification(p, player);
		}
	}

	private Block getTargetBlock(Player player) {
		return player.getTargetBlock(transparentBlocks, 8);
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