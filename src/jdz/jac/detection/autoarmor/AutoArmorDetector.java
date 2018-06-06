
package jdz.jac.detection.autoarmor;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.misc.utils.MaterialUtils;
import jdz.jac.JAC;
import jdz.jac.utils.PlayerPing;

public class AutoArmorDetector implements Listener {
	private final Set<Player> equipedOne = new HashSet<Player>();
	private final Set<Player> checked = new HashSet<Player>();

	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		ItemStack stack = event.getItem().getItemStack();
		if (!MaterialUtils.isArmour(stack.getType()))
			return;

		Player player = event.getPlayer();
		if (checked.contains(player))
			return;

		int ping = PlayerPing.getPing(player);
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
