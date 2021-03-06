
package jdz.jac.detection.autoarmor;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.components.events.Listener;
import jdz.bukkitUtils.utils.MaterialUtils;
import jdz.jac.JAC;
import jdz.jac.ping.PingFetcher;

public class AutoArmorDetector implements Listener {
	private final Set<Player> equipedOne = new HashSet<>();
	private final Set<Player> checked = new HashSet<>();

	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (!(event.getEntity() instanceof Player))
			return;

		ItemStack stack = event.getItem().getItemStack();
		if (!MaterialUtils.isArmour(stack.getType()))
			return;

		Player player = (Player) event.getEntity();
		if (checked.contains(player))
			return;

		int ping = PingFetcher.getPing(player);
		int pingTicks = (int) Math.ceil(ping / 50D);
		int slot = MaterialUtils.getArmourType(stack.getType()).ordinal();
		ItemStack item = player.getEquipment().getArmorContents()[slot];

		if (stack.equals(item))
			return;

		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			if (!stack.equals(player.getEquipment().getArmorContents()[slot]))
				return;

			if (!equipedOne.remove(player)) {
				equipedOne.add(player);
				return;
			}

			Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
				AutoArmorVerifier.verify(player);
			}, pingTicks);

			checked.add(player);
			Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
				checked.remove(player);
			}, 600L);

		}, AutoArmorConfig.getAutoequipTicks() + (AutoArmorConfig.isAddPingToTicks() ? pingTicks : ping));

	}
}
