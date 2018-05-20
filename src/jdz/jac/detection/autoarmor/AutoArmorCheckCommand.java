
package jdz.jac.detection.autoarmor;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.Command;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandPlayerOnly;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandUsage;

@CommandLabel("aac")
@CommandRequiredArgs(1)
@CommandUsage("aac {player} [strip|giveop]")
@CommandPlayerOnly
@CommandPermission("jac.admin")
public class AutoArmorCheckCommand extends Command{

	@Override
	public void execute(CommandSender sender, Set<String> flags, String... args) {
		Player player = Bukkit.getPlayer(args[0]);
		if (player == null || !player.isOnline()) {
			sender.sendMessage(ChatColor.RED+"That player is not online!");
			return;
		}
		
		if (args.length > 1 && args[1].toLowerCase().contains("give"))
			AutoArmorVerifier.verifyGive(player);
		else
			AutoArmorVerifier.verifyStrip(player);
	}

}
