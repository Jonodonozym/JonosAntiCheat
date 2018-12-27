
package jdz.jac.detection.aimbot.command;

import static jdz.jac.utils.Messager.message;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandOpOnly;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.jac.JAC;
import jdz.jac.detection.aimbot.AimbotClassifier;
import jdz.jac.detection.aimbot.aimData.DataManager;
import jdz.jac.detection.aimbot.aimData.DataSeries;
import jdz.jac.detection.aimbot.neuralNetwork.PredictResult;

@CommandLabel("stresstest")
@CommandOpOnly
@CommandUsage("[numSimultaneousChecks]")
public class AimbotStressTest extends SubCommand {

	@CommandMethod
	public void test(Player sender, int checks) {
		if (checks < 1)
			message(sender, "I think you should run more checks than that. This is a stress test, after all!");

		if (DataManager.isRecording(sender)) {
			message(sender, "You are already being measured, please wait about 10 seconds");
			return;
		}

		DataManager.startRecording(sender);
		message(sender, "Good to go, start hitting something for 15 seconds!");
		Bukkit.getScheduler().runTaskLaterAsynchronously(JAC.getInstance(), () -> {
			DataSeries data = DataManager.stopRecording(sender);

			long startTime = System.currentTimeMillis();

			PredictResult result = null;
			for (int i = 0; i < checks; i++)
				result = AimbotClassifier.classify(data);

			message(sender,
					"time for " + checks + " classifications: " + (System.currentTimeMillis() - startTime) + "ms");

			message(sender, "In case you are curious, you were classed as " + result.getBestMatched());
		}, 20L * 15);
	}

}
