
package jdz.jac.detection.wallhack;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.InventoryHolder;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.misc.WorldUtils;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;

public class WallhackDetector implements Listener {
	private static final HackType HACKTYPE_OPEN_CONTAINER = new HackType("Wallhacks",
			"Tried to open a container through a wall", Severity.HIGH, 50);

	@SuppressWarnings("unused") private static final HackType HACKTYPE_HIT_PLAYER = new HackType("Wallhacks",
			"Tried to hit a player through a wall", Severity.HIGH, 200); // TODO implement this

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onInventoryOpenEvent(InventoryOpenEvent event) {
		InventoryHolder holder;

		try {
			holder = event.getInventory().getHolder();
		}
		catch (Exception ex) {
			return;
		}

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

		Player player = (Player) event.getPlayer();
		if (WallChecker.isWallBetween(player, block)
				&& (connectedChest == null || WallChecker.isWallBetween(player, connectedChest))) {
			event.setCancelled(true);
			callEvent(player, block, HACKTYPE_OPEN_CONTAINER);
		}
	}

	private void callEvent(Player player, Block block, HackType type) {
		HackEvent event = new HackEvent(player, type);
		event.setLoggerData(getBlockExtraData(player, block, type));
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
