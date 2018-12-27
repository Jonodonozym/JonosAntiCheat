
package jdz.jac.notifier;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.config.AutoConfig;
import jdz.bukkitUtils.misc.Random;
import lombok.Getter;

public class NotifierConfig extends AutoConfig {
	@Getter private static boolean opEnabled = true;
	@Getter private static boolean consoleEnabled = true;
	@Getter private static List<String> permissions = new ArrayList<>();
	@Getter private static int cooldownSeconds = 1;

	@Getter private static boolean broadcastAutobans = true;
	@Getter private static boolean broadcastAutobansAdmin = true;

	private static List<String> autobanGloatMessages = new ArrayList<>();

	public NotifierConfig(Plugin plugin) {
		super(plugin, "notification");
	}

	public static String getRandomAutobanGloatMessage() {
		return autobanGloatMessages.get(Random.nextInt(autobanGloatMessages.size()));
	}
}
