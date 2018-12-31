
package jdz.jac.detection.aimbot.command;

import static jdz.jac.notifier.Messager.error;
import static jdz.jac.notifier.Messager.message;
import static jdz.jac.notifier.Messager.plainMessage;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

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
		message(sender, YELLOW + "Checking " + target.getName() + " for " + timeLength + " seconds.");
		try {
			AimbotClassifier.classify(target, (data, result) -> {
				plainMessage(sender, "** Analysis Report **");
				plainMessage(sender, GREEN + "  Best matched: " + YELLOW + result.getBestMatched());
				plainMessage(sender, GREEN + "  Euclidean distance: " + YELLOW + result.getDistance());
			}, () -> {
				error(sender, RED + "Insufficient data for an accurate classification");
				error(sender, RED + "Increase test length or ensure target remains in combat while checking");
			}, timeLength);
		}
		catch (IllegalStateException e2) {
			error(sender, RED + "Someone else is already checking them, please wait!");
		}
	}

}
