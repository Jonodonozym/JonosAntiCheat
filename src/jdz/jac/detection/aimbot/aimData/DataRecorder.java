
package jdz.jac.detection.aimbot.aimData;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.misc.CombatTimer;
import jdz.jac.ping.compensation.PlayerLocationHistory;

public class DataRecorder implements Listener {
	private static CombatTimer timer;

	public static void register(Plugin plugin, CombatTimer timer) {
		DataRecorder.timer = timer;
		new DataRecorder().registerEvents(plugin);
		DataIO.initFolders();
	}

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player && event.getEntity() instanceof LivingEntity))
			return;

		Player player = (Player) event.getDamager();

		if (!DataManager.isRecording(player))
			return;

		record(player, (LivingEntity) event.getEntity(), true);
	}

	@EventHandler
	public void onClickOnAir(PlayerInteractEvent e) {
		if (!DataManager.isRecording(e.getPlayer()))
			return;

		if (!timer.isInCombat(e.getPlayer()))
			return;

		LivingEntity target = timer.getLastAttacker(e.getPlayer());
		if (target == null)
			target = timer.getLastMobAttackerr(e.getPlayer());
		if (target != null)
			record(e.getPlayer(), target, false);
	}

	private void record(Player player, LivingEntity target, boolean hit) {
		DataSeries dataSeries = DataManager.getDataSeries(player);
		float angle = getLookAngle(player, target);

		if (!hit)
			dataSeries.addMiss(angle);
		else
			dataSeries.addHit(angle, getRange(player, target));
	}

	private float getLookAngle(Player player, LivingEntity target) {
		Vector playerLookDir = player.getEyeLocation().getDirection();
		Vector playerEyeLoc = player.getEyeLocation().toVector();
		Vector entityLoc = getLocationFromPerspective(target, player);

		Vector playerEntityVec = entityLoc.subtract(playerEyeLoc);

		return playerLookDir.angle(playerEntityVec);
	}

	private float getRange(Player player, LivingEntity target) {
		Vector playerEyeLoc = player.getEyeLocation().toVector();
		Vector entityLoc = getLocationFromPerspective(target, player);
		Vector entityEyeLoc = entityLoc.add(new Vector(0, target.getEyeHeight(), 0));

		return (float) Math.min(playerEyeLoc.distance(entityLoc), playerEyeLoc.distance(entityEyeLoc));
	}

	private Vector getLocationFromPerspective(Entity target, Player player) {
		if (target instanceof Player)
			return PlayerLocationHistory.getLocationFromPerspective((Player) target, player).toVector();
		return target.getLocation().toVector();
	}

}
