
package jdz.jac.ping;

import static org.bukkit.ChatColor.WHITE;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.Command;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;

@CommandLabel("ping")
public class PingCommand extends Command {

	@CommandMethod
	public void ping(Player sender) {
		ping(sender, sender);
	}

	@CommandMethod
	public void ping(Player sender, Player target) {
		int ping = PingFetcher.getPing(target);
		sender.sendMessage(target.getDisplayName() + WHITE + "'s ping: " + LIGHT_PURPLE + ping);
	}

}
