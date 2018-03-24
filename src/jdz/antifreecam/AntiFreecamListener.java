
package jdz.antifreecam;

import static org.bukkit.ChatColor.BOLD;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.RESET;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.util.BlockIterator;

import jdz.bukkitUtils.fileIO.FileLogger;
import jdz.bukkitUtils.misc.WorldUtils;

public class AntiFreecamListener implements Listener {
	private final AntiFreecamPlugin plugin;
	private final FileLogger logger;

	private final Set<Player> cooldownPlayers = new HashSet<Player>();

	public AntiFreecamListener(AntiFreecamPlugin plugin) {
		this.plugin = plugin;
		logger = new FileLogger(plugin);
	}

	@EventHandler
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		InventoryHolder holder = e.getInventory().getHolder();
		Block block = null;
		Block connectedChest = null;
		if (holder instanceof DoubleChest) {
			InventoryHolder holder2 = ((DoubleChest) holder).getLeftSide();
			block = ((BlockState)holder2).getBlock();
			holder2 = ((DoubleChest) holder).getRightSide();
			connectedChest = ((BlockState)holder2).getBlock();
		}
		else if (!(holder instanceof BlockState))
			return;
		else
			block = ((BlockState) holder).getBlock();

		if (AntiFreecamConfig.containersToIgnore.contains(block.getType()))
			return;
		
		Player player = (Player) e.getPlayer();
		Block target = getTargetBlock(player, block);

		if (!block.getLocation().equals(target.getLocation())) {
			if (connectedChest != null) {
				target = getTargetBlock(player, connectedChest);
				if (target.getLocation().equals(connectedChest.getLocation()))
					return;
			}

			e.setCancelled(true);

			if (AntiFreecamConfig.fileLogging)
				log(player, block, target);

			if (AntiFreecamConfig.consoleLogging)
				plugin.getLogger().info(player.getName()
						+ " Tried to open a container through a wall! Check AntiFreecam logs for details");

			if (cooldownPlayers.contains(player))
				return;
			cooldownPlayers.add(player);
			Bukkit.getScheduler().runTaskLater(plugin, () -> {
				cooldownPlayers.remove(player);
			}, AntiFreecamConfig.notifyCooldownTicks);

			for (Player p : Bukkit.getOnlinePlayers())
				if (shouldNotify(p, player))
					sendNotification(p, player);
		}
	}

	private Block getTargetBlock(Player player, Block interactedBlock) {
		BlockIterator bi = new BlockIterator(player, 8);
		Block block = null;
		while (bi.hasNext()) {
			block = bi.next();
			if (block.getLocation().equals(interactedBlock.getLocation()))
				return interactedBlock;
			if (AntiFreecamConfig.canClickThrough.contains(block.getType()))
				continue;
			return block;
		}
		return block;
	}

	private boolean shouldNotify(Player player, Player cheater) {
		if (player.isOp())
			return AntiFreecamConfig.notifyOP;
		if (player.equals(cheater))
			return false;
		for (String permission : AntiFreecamConfig.notifyPermission)
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
				+ " Tried to open a container through a wall!");
		player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
	}
}
