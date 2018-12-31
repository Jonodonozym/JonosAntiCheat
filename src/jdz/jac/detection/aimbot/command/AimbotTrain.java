
package jdz.jac.detection.aimbot.command;

import static jdz.jac.notifier.Messager.message;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.jac.detection.aimbot.AimbotConfig;
import jdz.jac.detection.aimbot.aimData.DataManager;
import jdz.jac.detection.aimbot.neuralNetwork.LVQTrainer;

@CommandLabel("train")
@CommandRequiredArgs(1)
@CommandShortDescription("trains the AI in a category e.g. legit, wurst, exodus")
@CommandUsage("{category} | {player} {category} {samples}")
@CommandPermission("jac.op")
public class AimbotTrain extends SubCommand {

	@CommandMethod
	public void execute(Player sender, String category) {
		execute(sender, sender, category, AimbotConfig.defaultTrainingSamples);
	}

	@CommandMethod
	public void execute(Player sender, Player target, String category, int samples) {
		if (DataManager.isRecording(sender)) {
			message(sender, RED + "Player already in a capturing process, please cancel it first!");
			return;
		}

		message(sender, YELLOW + "Started traning " + category);
		LVQTrainer.train(target, (message) -> {
			message(sender, message);
		}, category, samples);
	}
}
