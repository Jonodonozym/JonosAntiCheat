
package jdz.jac.detection.aimbot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.configuration.AutoConfig;

public class AimbotConfig extends AutoConfig {
	public static final int defaultTrainingSamples = 7;
	public static final int trainingSampleSeconds = 6;
	public static final double outlierThreshold = 0.5;
	public static final int minCheckClicks = 15;
	public static final double minRecheckIntervalSeconds = 120;
	private static final Set<String> allowedClasses = new HashSet<>(Arrays.asList("vanilla.legit"));

	public AimbotConfig(Plugin plugin) {
		super(plugin, "aimbot");
	}

	public static boolean isClassAllowed(String classification) {
		return classification.toLowerCase().startsWith("legit") || allowedClasses.contains(classification);
	}
}
