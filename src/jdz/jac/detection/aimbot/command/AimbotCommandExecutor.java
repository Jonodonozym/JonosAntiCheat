
package jdz.jac.detection.aimbot.command;

import java.util.Arrays;
import java.util.List;

import jdz.bukkitUtils.commands.CommandExecutor;
import jdz.bukkitUtils.commands.ParentCommand;
import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import lombok.Getter;

@CommandLabel("ffc")
@CommandLabel("aimbot")
@CommandLabel("aim")
public class AimbotCommandExecutor extends ParentCommand {
	@Getter protected List<SubCommand> subCommands = Arrays.asList(new AimbotCheck(), new AimbotInfo(),
			new AimbotTrain(), new AimbotTestMonster());

	public AimbotCommandExecutor(CommandExecutor commandExecutor) {
		super(commandExecutor);
		setDefaultCommand(new AimbotCheck());
	}
}
