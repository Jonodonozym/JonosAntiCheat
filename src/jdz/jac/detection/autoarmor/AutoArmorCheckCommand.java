
package jdz.jac.detection.autoarmor;

import static jdz.jac.utils.Messager.message;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandPermission;
import jdz.bukkitUtils.commands.annotations.CommandRequiredArgs;
import jdz.bukkitUtils.commands.annotations.CommandShortDescription;
import jdz.bukkitUtils.commands.annotations.CommandUsage;

@CommandLabel("aac")
@CommandLabel("autoarmorcheck")
@CommandRequiredArgs(1)
@CommandShortDescription("checks a player for auto-armor")
@CommandUsage("{player}")
@CommandPermission("jac.admin")
public class AutoArmorCheckCommand extends SubCommand {

	@CommandMethod
	public void execute(Player sender, Player target) {
		message(sender, "Checking " + target.getName() + " for auto-armor...");
		AutoArmorVerifier.verify(sender, target);
	}

}
