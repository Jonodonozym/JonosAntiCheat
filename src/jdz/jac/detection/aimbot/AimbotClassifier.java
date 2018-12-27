
package jdz.jac.detection.aimbot;

import static jdz.jac.detection.aimbot.AimbotConfig.minCheckClicks;
import static jdz.jac.utils.Messager.message;
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

	public static void classify(Player target, Callback callback, int sampleSeconds) {
		if (DataManager.isRecording(target))
			throw new IllegalStateException("Already checking the target!");

		DataManager.startRecording(target);
		Bukkit.getScheduler().runTaskLaterAsynchronously(JAC.getInstance(), () -> {
			DataSeries data = DataManager.stopRecording(target);

			if (data.getLength() < minCheckClicks)
				throw new InsufficientDataException();

			PredictResult result = classify(data);
			callback.onClassify(result);
		}, 20L * sampleSeconds);
	}

	public static class InsufficientDataException extends RuntimeException {
		private static final long serialVersionUID = -5650900306799452375L;
	}

	public static PredictResult classify(DataSeries data) {
		return lvq.predict(data.getAllDump());
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
		message(p, AQUA + "  Neural network: ");
		message(p, "   Input layer size: " + YELLOW + lvq.getInputLayerSize());
		message(p, "   Output layer size: " + YELLOW + lvq.getOutputLayerSize());
		lvq.printOutputLayers();
		DataIO.sendInfoToPlayer(p);
	}
}
