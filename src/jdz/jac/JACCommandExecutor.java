
package jdz.jac;

import static lombok.AccessLevel.PROTECTED;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import jdz.bukkitUtils.commands.CommandExecutor;
import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandExecutorPermission;
import lombok.Getter;

@CommandExecutorPermission("jac.admin")
public class JACCommandExecutor extends CommandExecutor {
	@Getter(value = PROTECTED) private final List<SubCommand> subCommands = new ArrayList<>();

	public JACCommandExecutor(JavaPlugin plugin) {
		super(plugin, "jac");
	}

	public void add(SubCommand command) {
		subCommands.add(command);
	}
}
