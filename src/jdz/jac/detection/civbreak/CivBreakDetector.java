
package jdz.jac.detection.civbreak;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.misc.utils.MaterialUtils;
import jdz.bukkitUtils.misc.utils.MaterialUtils.ResourceType;
import jdz.bukkitUtils.misc.utils.MaterialUtils.ToolType;
import jdz.jac.JAC;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;

public class CivBreakDetector implements Listener {
	private static final HackType HACKTYPE_CIV_BREAK = new HackType("CivBreak", "Broke the nexus too fast",
			Severity.NORMAL, 20);

	private final Map<Player, Set<Block>> breakLock = new HashMap<Player, Set<Block>>();
	private final Set<Player> hasClicked = new HashSet<Player>();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		breakLock.put(event.getPlayer(), new HashSet<Block>());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		breakLock.remove(event.getPlayer());
	}

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

		int delay = (int) (getBreakTicks(player) * 0.75);
		if (delay > 0) {
			breakLock.get(player).add(event.getClickedBlock());
			Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
				if (breakLock.containsKey(player))
					breakLock.get(player).remove(event.getClickedBlock());
			}, delay);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
	public void onClickBlock(BlockBreakEvent event) {
		Player player = event.getPlayer();
		if (player.getGameMode().equals(GameMode.CREATIVE))
			return;

		if (breakLock.get(player).contains(event.getBlock())
				|| (event.getBlock().getType() == Material.ENDER_STONE && !hasClicked.contains(player))) {
			event.setCancelled(true);
			new HackEvent(player, HACKTYPE_CIV_BREAK).call();
		}

		hasClicked.remove(player);
	}

	public int getBreakTicks(Player player) {
		ItemStack item = player.getItemInHand();
		if (!MaterialUtils.isTool(item.getType()) || MaterialUtils.getToolType(item.getType()) != ToolType.PICKAXE)
			return 300;

		int hasteLevels = 0;
		for (PotionEffect pe : player.getActivePotionEffects()) {
			if (!pe.getType().equals(PotionEffectType.FAST_DIGGING))
				continue;
			hasteLevels = pe.getAmplifier();
		}

		int efficiencyLevels = item.getEnchantmentLevel(Enchantment.DIG_SPEED);

		int damagePerTick = speedMult(MaterialUtils.getResource(item.getType()));
		if (efficiencyLevels > 0)
			damagePerTick += efficiencyLevels * efficiencyLevels + 1;
		damagePerTick *= 1 + (hasteLevels * 0.2);

		if (damagePerTick > 90)
			return 0;
		return 90 / damagePerTick;
	}

	@SuppressWarnings("incomplete-switch")
	private int speedMult(ResourceType type) {
		if (type != null)
			switch (type) {
			case DIAMOND:
				return 8;
			case GOLD:
				return 12;
			case IRON:
				return 6;
			case STONE:
				return 4;
			case WOOD:
				return 2;
			}
		return 1;
	}
}
