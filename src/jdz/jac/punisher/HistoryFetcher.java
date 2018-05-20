package jdz.jac.punisher;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.events.Listener;
import lombok.Getter;

public class HistoryFetcher implements Listener {
	@Getter private static final HistoryFetcher instance = new HistoryFetcher();

	private HistoryFetcher() {}

	public static int getBans(Player player, BanLevel minLevel) {
		return 0; // TODO
	}
}
