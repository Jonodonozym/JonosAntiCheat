
package jdz.jac.detection.aimbot.command;

import static jdz.jac.utils.Messager.message;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.jac.detection.aimbot.AimbotClassifier;
import jdz.jac.detection.aimbot.AimbotClassifier.InsufficientDataException;

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
			AimbotClassifier.classify(target, (result) -> {
				message(sender, "** Analysis Report **");
				message(sender, GREEN + "  Best matched: " + YELLOW + result.getBestMatched());
				message(sender, GREEN + "  Euclidean distance: " + YELLOW + result.getDistance());
			}, timeLength);
		}
		catch (InsufficientDataException e1) {
			message(sender, RED + "Insufficient data for an accurate classification");
			message(sender, RED + "Increase test length or ensure target remains in combat while checking");
		}
		catch (IllegalStateException e2) {
			message(sender, RED + "Someone else is already checking them, please wait!");
		}
	}

}
