
package jdz.jac.detection.wallhack;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.misc.WorldUtils;
import jdz.jac.hackingEvent.HackEvent;
import jdz.jac.hackingEvent.HackType;
import jdz.jac.logger.Logger;

public class WallhackListener implements Listener {
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onInventoryOpenEvent(InventoryOpenEvent e) {
		InventoryHolder holder = e.getInventory().getHolder();
		Block block = null;
		Block connectedChest = null;

		if (holder instanceof DoubleChest) {
			InventoryHolder holder2 = ((DoubleChest) holder).getLeftSide();
			block = ((BlockState) holder2).getBlock();
			holder2 = ((DoubleChest) holder).getRightSide();
			connectedChest = ((BlockState) holder2).getBlock();
		}
		else if (!(holder instanceof BlockState))
			return;
		else
			block = ((BlockState) holder).getBlock();

		if (WallhackConfig.containersToIgnore.contains(block.getType()))
			return;

		Player player = (Player) e.getPlayer();
		if (WallChecker.isWallBetween(player, block)
				&& (connectedChest == null || WallChecker.isWallBetween(player, connectedChest))) {
			e.setCancelled(true);
			new HackEvent(player, HackType.WALLHACK_OPEN_CONTAINER).call();
			logBlockTargetWallHack(player, block);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBlockBreak(BlockBreakEvent event) {
		if (WallChecker.isWallBetween(event.getPlayer(), event.getBlock())) {
			event.setCancelled(true);
			new HackEvent(event.getPlayer(), HackType.WALLHACK_BREAK_BLOCK);
			logBlockTargetWallHack(event.getPlayer(), event.getBlock());
		}
	}

	private String getLongDescription(HackType type, Player hacker, Block interactedBlock) {
		Block block = WallChecker.getTarget(hacker, interactedBlock);
		return hacker.getName() + " " + type.getActionDescription() + "\n\tPlayer location: "
				+ WorldUtils.locationToLegibleString(hacker.getLocation()) + "\n\tPlayer block in sight: "
				+ WorldUtils.locationToLegibleString(block.getLocation()) + "\n\tBlock in sight type: "
				+ block.getType().name();
	}

	private void logBlockTargetWallHack(Player player, Block target) {
		Logger.getGeneral()
				.log(getLongDescription(HackType.WALLHACK_OPEN_CONTAINER, player, target)
						+ "\n\tInteracted block location: " + WorldUtils.locationToLegibleString(target.getLocation())
						+ "\n\tInteracted block type: " + target.getType());
	}
}
