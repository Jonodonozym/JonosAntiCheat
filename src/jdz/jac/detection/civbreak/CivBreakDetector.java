
package jdz.jac.detection.civbreak;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.events.Listener;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;
import jdz.jac.punisher.AutobanEvent;

public class CivBreakDetector implements Listener {
	private static final HackType HACKTYPE_CIV_BREAK = new HackType("CivBreak", "Broke the nexus too fast",
			Severity.NORMAL, 30);

	private final Set<Player> hasClicked = new HashSet<>();
	private final Set<Player> bannedPlayers = new HashSet<>();

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onClickBlock(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;

		if (!CivBreakConfig.isEnabled())
			return;

		Player player = event.getPlayer();
		if (player.getGameMode() == GameMode.CREATIVE)
			return;

		if (!event.getClickedBlock().getType().equals(Material.ENDER_STONE))
			return;

		hasClicked.add(player);
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onBreakNexus(BlockBreakEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode().equals(GameMode.CREATIVE))
			return;

		if (bannedPlayers.contains(player)) {
			event.setCancelled(true);
			return;
		}

		else if (event.getBlock().getType() == Material.ENDER_STONE && !hasClicked.contains(player)) {
			new HackEvent(player, HACKTYPE_CIV_BREAK, "(No Click)").call();
			event.setCancelled(true);
		}

		hasClicked.remove(player);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onAutoban(AutobanEvent event) {
		if (!event.getType().equals(HACKTYPE_CIV_BREAK))
			return;

		ItemStack handItem = event.getPlayer().getItemInHand();
		event.setExtraData("Held item: " + handItem.getType());

		bannedPlayers.add(event.getPlayer());
	}
}
