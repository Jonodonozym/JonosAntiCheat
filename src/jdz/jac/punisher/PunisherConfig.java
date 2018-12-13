package jdz.jac.punisher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;

import jdz.bukkitUtils.events.Listener;
import jdz.bukkitUtils.events.custom.ConfigReloadEvent;
import jdz.jac.JAC;
import jdz.jac.detection.HackType;
import lombok.Getter;

public class PunisherConfig implements Listener {
	private static final Map<HackType, BanLevel> hackTypeToBanLevel = new HashMap<HackType, BanLevel>();
	private static final Set<HackType> autobanEnabled = new HashSet<HackType>();
	private static final Map<BanLevel, Integer> tempbanLevelDays = new HashMap<BanLevel, Integer>();
	private static final Map<BanLevel, Integer> unbanTier = new HashMap<BanLevel, Integer>();
	private static final Map<BanLevel, Integer> bansTilPerm = new HashMap<BanLevel, Integer>();

	@Getter private static boolean runCommands = false;
	@Getter private static boolean kickInstead = false;

	public PunisherConfig(Plugin plugin) {}

	static BanLevel getBanLevel(HackType type) {
		if (!hackTypeToBanLevel.containsKey(type))
			hackTypeToBanLevel.put(type, BanLevel.HEAVY);
		return hackTypeToBanLevel.get(type);
	}

	static Integer getTempBanDays(BanLevel level) {
		return tempbanLevelDays.containsKey(level) ? tempbanLevelDays.get(level) : -1;
	}

	static Integer getUnbanTier(BanLevel level) {
		return unbanTier.get(level);
	}

	static Integer getBansTilPerm(BanLevel level) {
		return bansTilPerm.containsKey(level) ? bansTilPerm.get(level) : 1;
	}

	static boolean autobanEnabled(HackType type) {
		return autobanEnabled.contains(type);
	}

	@EventHandler
	public void onConfigReload(ConfigReloadEvent event) {
		if (event.getPlugin() != JAC.getInstance())
			return;

		FileConfiguration config = event.getConfig();

		runCommands = config.getBoolean("banning.runCommands");
		kickInstead = config.getBoolean("banning.kickInstead");

		autobanEnabled.clear();
		for (HackType type : HackType.getAllTypes()) {
			hackTypeToBanLevel.put(type, BanLevel.valueOf(config.getString(type.getName() + ".level", "HEAVY")));
			if (config.getBoolean(type.getName() + ".autoban", false))
				autobanEnabled.add(type);
		}

		for (BanLevel level : BanLevel.temp()) {
			tempbanLevelDays.put(level, config.getInt("banning.lengthDays." + level.name(), 7));
			bansTilPerm.put(level, config.getInt("banning.bansTilPerm." + level.name(), 2));
		}

		for (BanLevel level : BanLevel.values())
			unbanTier.put(level, config.getInt("banning.unbanTier." + level.name(), 3));
	}
}
