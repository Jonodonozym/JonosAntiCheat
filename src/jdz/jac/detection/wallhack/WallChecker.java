
package jdz.jac.detection.wallhack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class WallChecker {
	public static boolean isWallBetween(Player player, Location loc) {
		return isWallBetween(player, loc.getBlock());
	}

	public static boolean isWallBetween(Player player, Block interactedBlock) {
		return !getTarget(player, interactedBlock).equals(interactedBlock);
	}

	public static Block getTarget(Player player, Location lookingAt) {
		return getTarget(player, lookingAt.getBlock());
	}

	public static Block getTarget(Player player, Block interactedBlock) {
		int dist = (int) player.getEyeLocation().distance(interactedBlock.getLocation()) + 1;
		BlockIterator bi = new BlockIterator(player, dist);
		Block block = null;
		while (bi.hasNext()) {
			block = bi.next();
			if (block.getLocation().equals(interactedBlock.getLocation()))
				return interactedBlock;
			if (canClickThrough(block.getType()))
				continue;
			return block;
		}
		return interactedBlock;
	}

	private static boolean canClickThrough(Material mat) {
		return !mat.isOccluding() || mat.isTransparent() || WallhackConfig.getCanClickThrough().contains(mat);
	}
}
