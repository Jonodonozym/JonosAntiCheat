package jdz.jac.util;

import java.util.List;

import org.bukkit.entity.Player;
import org.guildcraft.gcbanz.GCBanz;
import org.guildcraft.gcbanz.data.Violation;
import org.guildcraft.gcbanz.data.persistence.SQL;

import lombok.Getter;

public class HistoryFetcher implements Listener {
	@Getter private static final HistoryFetcher instance = new HistoryFetcher();

	private HistoryFetcher() {}

	public static int getBans(Player player, BanLevel minLevel) {
		int numBans = 0;
		List<Violation> violations = GCBanz.sql.getHistory(player.getName());
		for(Violation violation: violations) {
			violation.reason
		}
	}
}
