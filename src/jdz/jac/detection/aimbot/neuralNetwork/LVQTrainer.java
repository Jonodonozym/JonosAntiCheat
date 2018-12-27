
package jdz.jac.detection.aimbot.neuralNetwork;

import static jdz.jac.detection.aimbot.AimbotConfig.outlierThreshold;
import static jdz.jac.detection.aimbot.AimbotConfig.defaultTrainingSamples;
import static jdz.jac.detection.aimbot.AimbotConfig.trainingSampleSeconds;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.YELLOW;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import jdz.jac.JAC;
import jdz.jac.detection.aimbot.AimbotClassifier;
import jdz.jac.detection.aimbot.aimData.DataIO;
import jdz.jac.detection.aimbot.aimData.DataManager;
import lombok.RequiredArgsConstructor;

public class LVQTrainer {
	public static void train(Player target, Logger logger, String category) {
		train(target, logger, category, defaultTrainingSamples);
	}

	public static void train(Player target, Logger logger, String category, int samples) {
		new TrainingTask(target, logger, category, samples).runTaskTimer(JAC.getInstance(), 0L,
				20L * trainingSampleSeconds);
	}

	public static interface Logger {
		public void log(String message);
	}

	@RequiredArgsConstructor
	private static class TrainingTask extends BukkitRunnable {
		private final Player target;
		private final Logger logger;
		private final String category;
		private final int trainingPhases;

		private int currentSample = 0;
		private List<DataSet> samples = new ArrayList<DataSet>();

		@Override
		public void run() {
			if (currentSample > 0) {
				Double[] sampleData = DataManager.getDataSeries(target).getAllDump();

				if (isOutlier(sampleData)) {
					logger.log(YELLOW + "Outlier detected at sample " + currentSample + ". Resampling...");
					DataManager.clearData(target);
					return;
				}

				logger.log(GREEN + "Finished sampling " + currentSample);
				samples.add(new DataSet(category, sampleData));
				DataManager.clearData(target);
			}

			if (currentSample >= trainingPhases) {
				logger.log(GREEN + "Sample process finished");
				DataManager.stopRecording(target);
				save();
				rebuildNetwork();
				cancel();
				return;
			}

			currentSample++;
			logger.log(
					YELLOW + "Sampling features for " + category + ", sample " + currentSample + "/" + trainingPhases);
			DataManager.startRecording(target);
		}

		private boolean isOutlier(Double[] sampleData) {
			if (samples.size() >= 1)
				for (int i = 0; i <= sampleData.length - 1; i++) {
					double delta = Math.abs(sampleData[i] - samples.get(samples.size() - 1).getData()[i]);
					if (delta >= outlierThreshold)
						return true;
				}
			return false;
		}

		private void save() {
			try {
				DataIO.saveCategory(category, samples);
				logger.log(YELLOW + "Sample saved to " + category);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.log(RED + "Failed to save model " + category + ", check the console");
			}
		}

		private void rebuildNetwork() {
			logger.log(GREEN + "Attempting to rebuild neural network...");
			try {
				int epochs = AimbotClassifier.rebuild();
				logger.log(GREEN + "Rebuilt neural network with epoch(es) " + epochs);
			}
			catch (Exception e) {
				e.printStackTrace();
				logger.log(RED + "Failed to build neural network, check the console");
			}
		}
	}
}