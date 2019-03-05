
package jdz.jac.detection.wallhack;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import jdz.bukkitUtils.components.events.Listener;
import jdz.bukkitUtils.utils.WorldUtils;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;

public class WallhackContainerDetector implements Listener {
	private static final HackType HACKTYPE_OPEN_CONTAINER = new HackType("Wallhacks",
			"Tried to open a container through a wall", Severity.HIGH, 50);

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onInventoryOpenEvent(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();

		InventoryHolder holder = event.getInventory().getHolder();
		if (holder == null)
			return;

		Block block = null;

		if (holder instanceof DoubleChest) {
			DoubleChest chest = (DoubleChest) holder;
			block = ((BlockState) chest.getLeftSide()).getBlock();
			if (canOpenDoubleChest(player, chest))
				return;
		}
		else if (holder instanceof BlockState) {
			block = ((BlockState) holder).getBlock();
			if (canOpen(player, block))
				return;
		}
		else
			return;

		event.setCancelled(true);
		callEvent(player, block, HACKTYPE_OPEN_CONTAINER);
	}

	private boolean canOpenDoubleChest(Player player, DoubleChest chest) {
		Block left = ((BlockState) chest.getLeftSide()).getBlock();
		Block right = ((BlockState) chest.getLeftSide()).getBlock();

		return !(canOpen(player, left) && canOpen(player, right));
	}

	private boolean canOpen(Player player, Block block) {
		if (WallhackConfig.getContainersToIgnore().contains(block.getType()))
			return true;

		return WallChecker.isWallBetween(player, block);
	}

	private void callEvent(Player player, Block block, HackType type) {
		HackEvent event = new HackEvent(player, type, getBlockExtraData(player, block, type));
		event.call();
	}

	private String getBlockExtraData(Player player, Block target, HackType type) {
		return getExtraData(type, player, target) + "\n\tInteracted block location: "
				+ WorldUtils.locationToLegibleString(target.getLocation()) + "\n\tInteracted block type: "
				+ target.getType();
	}

	private String getExtraData(HackType type, Player hacker, Block interactedBlock) {
		Block block = WallChecker.getTarget(hacker, interactedBlock);
		return hacker.getName() + " " + type.getActionDescription() + "\n\tPlayer location: "
				+ WorldUtils.locationToLegibleString(hacker.getLocation()) + "\n\tPlayer block in sight: "
				+ WorldUtils.locationToLegibleString(block.getLocation()) + "\n\tBlock in sight type: "
				+ block.getType().name();
	}
}
