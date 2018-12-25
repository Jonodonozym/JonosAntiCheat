
package jdz.jac.detection.aimbot.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandSync;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.jac.JAC;

@CommandLabel("m")
@CommandUsage("[seconds]")
@CommandShortDescription("spawns an invincible zombie at your cursor for training on")
@CommandSync
@CommandPermission("jac.op")
public class AimbotTestMonster extends SubCommand {
	@CommandMethod
	public void execute(Player player) {
		execute(player, 60);
	}

	@CommandMethod
	public void execute(Player player, int seconds) {
		Entity entity = player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.ZOMBIE);
		((LivingEntity) entity).setMaxHealth(2000);
		((LivingEntity) entity).setHealth(2000);
		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			entity.remove();
		}, 20 * seconds);
	}
}
