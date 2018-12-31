
package jdz.jac.detection.autoarmor;

import static jdz.jac.notifier.Messager.message;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import jdz.jac.JAC;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;
import jdz.jac.ping.PingFetcher;

public class AutoArmorVerifier {
	private static HackType HACKTYPE_AUTO_ARMOR = new HackType("AutoArmor", "equiped items too fast!", Severity.NORMAL,
			400);

	public static void verify(Player player) {
		verify(null, player);
	}

	public static void verify(Player checker, Player player) {
		ItemStack[] equipment = player.getEquipment().getArmorContents();

		int nulls = 0;
		for (int i = 0; i < 4; i++)
			if (equipment[i] == null || equipment[i].getType() == Material.AIR)
				nulls++;

		if (nulls > 0) {
			if (checker != null)
				message(checker, "Player isn't wearing full gear. Check again when they are fullly armored");
			return;
		}

		for (int i = 0; i < 4; i++)
			player.getInventory().setItem(i + 27, equipment[i]);
		player.getInventory().setArmorContents(null);

		int ping = PingFetcher.getPing(player);
		int ticks = AutoArmorConfig.getAutoequipTicks() + ping / 25;

		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			int equipedPieces = 0;

			for (int i = 0; i < 4; i++)
				if (equipment[i].equals(player.getEquipment().getArmorContents()[i]))
					equipedPieces++;

			player.getEquipment().setArmorContents(equipment);
			for (int i = 0; i < 4; i++)
				player.getInventory().setItem(i + 27, null);

			if (equipedPieces == 4) {
				if (checker != null)
					message(checker, "Player auto-equiped check passed [CheckTime:" + ticks * 50 + "ms][Ping:" + ping
							+ "ms][items:" + equipedPieces + "]");
				else {
					HackEvent event = new HackEvent(player, HACKTYPE_AUTO_ARMOR,
							"[CheckTime:" + ticks * 50 + "ms][Ping:" + ping + "ms][items:" + equipedPieces + "]");
					event.call();
				}
			}
			else if (checker != null)
				message(checker, "Player auto-equiped check failed [CheckTime:" + ticks * 50 + "ms][Ping:" + ping
						+ "ms][items:" + equipedPieces + "]");
		}, ticks);
	}
}

