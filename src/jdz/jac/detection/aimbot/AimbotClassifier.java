
package jdz.jac.detection.aimbot;

import static jdz.jac.detection.aimbot.AimbotConfig.minCheckClicks;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.YELLOW;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import jdz.jac.JAC;
import jdz.jac.detection.aimbot.aimData.DataIO;
import jdz.jac.detection.aimbot.aimData.DataManager;
import jdz.jac.detection.aimbot.aimData.DataSeries;
import jdz.jac.detection.aimbot.neuralNetwork.DataSet;
import jdz.jac.detection.aimbot.neuralNetwork.LVQNeuralNetwork;
import jdz.jac.detection.aimbot.neuralNetwork.PredictResult;
import lombok.RequiredArgsConstructor;

/**
 * Re-assembly of NascentNova's machine learning detection
 * 
 * https://www.spigotmc.org/threads/machine-learning-killaura-detection-in-minecraft.301609/
 * https://github.com/Nova41/SnowLeopard
 * 
 * TODO info commands, add automatic checking, wire all
 * together
 */
@RequiredArgsConstructor
public class AimbotClassifier {
	private static LVQNeuralNetwork lvq;

	static {
		try {
			rebuild();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void classify(Player target, Callback callback, Runnable onInsufficientData, int sampleSeconds) {
		DataManager.addPlayer(target);
		Bukkit.getScheduler().runTaskLater(JAC.getInstance(), () -> {
			DataSeries data = DataManager.removePlayer(target);

			if (data.getLength() < minCheckClicks) {
				onInsufficientData.run();
				return;
			}

			PredictResult result = lvq.predict(data.getAllDump());
			callback.onClassify(result);
		}, 20L * sampleSeconds);
	}

	public static interface Callback {
		public void onClassify(PredictResult result);
	}

	public static int rebuild() throws Exception {
		lvq = new LVQNeuralNetwork(0.2, 0.95);
		for (DataSet dataset : DataIO.getAllCategory())
			lvq.input(dataset);

		if (lvq.getInputFeatures() == 0)
			return -1;

		lvq.normalize();
		lvq.initialize();
		return lvq.trainUntil(0.00000000001);
	}

	public static void sendInfoToPlayer(Player p) {
		p.sendMessage(AQUA + "  Neural network: ");
		p.sendMessage("   Input layer size: " + YELLOW + lvq.getInputLayerSize());
		p.sendMessage("   Output layer size: " + YELLOW + lvq.getOutputLayerSize());
		lvq.printOutputLayers();
		DataIO.sendInfoToPlayer(p);
	}
}