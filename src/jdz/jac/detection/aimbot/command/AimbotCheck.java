
package jdz.jac.detection.aimbot.command;

import static org.bukkit.ChatColor.*;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.jac.detection.aimbot.AimbotClassifier;

@CommandLabel("check")
@CommandUsage("{target} [seconds]")
public class AimbotCheck extends SubCommand {

	@CommandMethod
	public void execute(Player sender, Player target) {
		execute(sender, target, 15);
	}

	@CommandMethod
	public void execute(Player sender, Player target, int timeLength) {
		sender.sendMessage(YELLOW + "Checking " + target.getName() + " for " + timeLength + " seconds.");
		AimbotClassifier.classify(target, (result) -> {
			sender.sendMessage(GOLD + "** Analysis Report **");
			sender.sendMessage(GREEN + "  Best matched: " + YELLOW + result.getBestMatched());
			sender.sendMessage(GREEN + "  Euclidean distance: " + YELLOW + result.getDistance());
		}, () -> {
			sender.sendMessage(RED + "Insufficient data for an accurate classification");
			sender.sendMessage(RED + "Increase test length or ensure target remains in combat while checking");
		}, timeLength);
	}

}
