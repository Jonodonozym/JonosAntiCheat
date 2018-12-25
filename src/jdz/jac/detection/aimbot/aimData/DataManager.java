
package jdz.jac.detection.aimbot.aimData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;

public class DataManager {
	private static Map<UUID, DataSeries> playerData = new HashMap<>();

	public static void addPlayer(OfflinePlayer player) {
		playerData.put(player.getUniqueId(), new DataSeries());
	}

	public static DataSeries removePlayer(OfflinePlayer player) {
		return playerData.remove(player.getUniqueId());
	}

	public static boolean isRecording(OfflinePlayer player) {
		return playerData.containsKey(player.getUniqueId());
	}

	public static void clearData(OfflinePlayer player) {
		playerData.get(player.getUniqueId()).clear();
	}

	public static DataSeries getDataSeries(OfflinePlayer player) {
		return playerData.get(player.getUniqueId());
	}
}
