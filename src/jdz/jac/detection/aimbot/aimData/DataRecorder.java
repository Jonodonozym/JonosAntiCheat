
package jdz.jac.detection.aimbot.aimData;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.misc.CombatTimer;
import jdz.jac.detection.pingCompensation.PlayerLocationHistory;
import jdz.jac.ping.PingFetcher;

public class DataRecorder implements Listener {
	private static CombatTimer timer;

	public static void register(Plugin plugin, CombatTimer timer) {
		DataRecorder.timer = timer;
		new DataRecorder().registerEvents(plugin);
		DataIO.initFolders();
	}

	@EventHandler
	public void onAttack(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;

		Player player = (Player) event.getDamager();

		if (!DataManager.isRecording(player))
			return;

		record(player, event.getEntity(), true);
	}

	@EventHandler
	public void onClickOnAir(PlayerInteractEvent e) {
		if (!DataManager.isRecording(e.getPlayer()))
			return;

		if (!timer.isInCombat(e.getPlayer()))
			return;

		Entity target = timer.getLastAttacker(e.getPlayer());
		if (target == null)
			target = timer.getLastMobAttackerr(e.getPlayer());
		if (target != null)
			record(e.getPlayer(), target, false);
	}

	private void record(Player player, Entity target, boolean hit) {
		DataSeries dataSeries = DataManager.getDataSeries(player);

		Vector playerLookDir = player.getEyeLocation().getDirection();
		Vector playerEyeLoc = player.getEyeLocation().toVector();

		Vector entityLoc = target.getLocation().toVector();

		if (target instanceof Player) {
			int ping = PingFetcher.getPing(player);
			entityLoc = PlayerLocationHistory.getLocationMSAgo(player, ping).toVector();
		}

		Vector playerEntityVec = entityLoc.subtract(playerEyeLoc);

		float angle = playerLookDir.angle(playerEntityVec);

		// removing outliers
		if (Math.abs(angle) > Math.PI / 3)
			return;

		dataSeries.add(angle, hit);
	}

}
