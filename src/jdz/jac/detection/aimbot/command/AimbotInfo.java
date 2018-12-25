
package jdz.jac.detection.aimbot.command;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.jac.detection.aimbot.AimbotClassifier;

@CommandLabel("info")
public class AimbotInfo extends SubCommand {

	@CommandMethod
	public void sendInfoToPlayer(Player player) {
		AimbotClassifier.sendInfoToPlayer(player);
	}
}
