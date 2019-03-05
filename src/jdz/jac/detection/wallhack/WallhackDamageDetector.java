
package jdz.jac.detection.wallhack;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import jdz.bukkitUtils.components.events.Listener;
import jdz.bukkitUtils.utils.WorldUtils;
import jdz.jac.detection.HackEvent;
import jdz.jac.detection.HackType;
import jdz.jac.detection.Severity;
import jdz.jac.ping.compensation.PlayerLocationHistory;

public class WallhackDamageDetector implements Listener {
	private static final HackType HACKTYPE_HIT_PLAYER = new HackType("Wallhacks",
			"Tried to hit a player through a wall", Severity.NONE, 200);

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player && event.getEntity() instanceof Player))
			return;

		Player player = (Player) event.getDamager();
		Player target = (Player) event.getEntity();

		Block block = player.getTargetBlock(null, 8);
		if (block == null)
			return;

		Location eyeLoc = player.getEyeLocation();

		Location targetLoc = PlayerLocationHistory.getLocationFromPerspective(target, player);
		Location targetEyeLoc = PlayerLocationHistory.getEyeLocationFromPerspective(target, player);
		Location blockLoc = block.getLocation().add(0.5, 0.5, 0.5);

		double d1 = eyeLoc.distance(targetLoc);
		double d2 = eyeLoc.distance(targetEyeLoc);
		double d3 = eyeLoc.distance(blockLoc);

		if (d3 > d1 && d3 > d2) {
			event.setCancelled(true);
			new HackEvent(player, HACKTYPE_HIT_PLAYER,
					getExtraData(player, targetLoc, targetEyeLoc, blockLoc, d1, d2, d3)).call();
		}
	}

	private String getExtraData(Player hacker, Location targetLocation, Location targetEyeLocation, Location blockLoc,
			double d1, double d2, double d3) {
		return hacker.getName() + " " + HACKTYPE_HIT_PLAYER.getActionDescription() + "\n\tPlayer eye location: "
				+ WorldUtils.locationToLegibleString(hacker.getEyeLocation()) + "\n\target location: "
				+ WorldUtils.locationToLegibleString(targetLocation) + "\n\target eye location: "
				+ WorldUtils.locationToLegibleString(targetEyeLocation) + "\n\tPlayer block in sight: "
				+ WorldUtils.locationToLegibleString(blockLoc) + "\n\t(" + d1 + "," + d2 + "," + d3 + ")";
	}
}
