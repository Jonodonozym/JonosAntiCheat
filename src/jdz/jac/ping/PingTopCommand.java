package jdz.jac.ping;

import static org.bukkit.ChatColor.GRAY;
import static org.bukkit.ChatColor.GREEN;
import static org.bukkit.ChatColor.LIGHT_PURPLE;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.Command;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.components.Pair;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;

@CommandLabel("pingtop")
public class PingTopCommand extends Command {
	private static final int ITEMS_PER_PAGE = 10;

	@CommandMethod
	public void pingTop(Player sender) {
		pingTop(sender, 1);
	}

	@CommandMethod
	public void pingTop(Player sender, int page) {
		int pageIndex = page - 1;
		if (pageIndex < 0)
			pageIndex = 0;

		int maxPages = Math.max(0, PingTop.size() / ITEMS_PER_PAGE);
		if (pageIndex > maxPages)
			pageIndex = maxPages;

		List<String> messages = new ArrayList<>();

		messages.add(GRAY + "======== " + "Pingtop " + GREEN + "Page " + (pageIndex + 1) + "/" + (maxPages + 1) + GRAY
				+ " =======");

		if (sender instanceof Player) {
			int index = PingTop.getPosition(sender);
			if (index != -1) {
				messages.add(getLine(index));
				messages.add("");
			}
		}

		int startIndex = pageIndex * ITEMS_PER_PAGE;
		int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, PingTop.size());

		for (int i = startIndex; i < endIndex; i++)
			messages.add(getLine(i));

		sender.sendMessage(messages.toArray(new String[messages.size()]));
		sender.spigot().sendMessage(getNavigationLine(pageIndex, maxPages));
	}

	private String getLine(int rank) {
		Pair<Player, Integer> entry = PingTop.getByRank(rank);
		String s = "[" + (rank + 1) + "] " + GREEN + entry.getKey().getName() + LIGHT_PURPLE + " " + entry.getValue();
		return s;
	}

	private TextComponent getNavigationLine(int pageIndex, int maxPages) {
		TextComponent border = new TextComponent(GRAY + "========");
		TextComponent previous, next;
		if (pageIndex > 0) {
			previous = new TextComponent("[<<<<]");
			previous.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/pingtop " + (pageIndex - 1)));
		}
		else
			previous = new TextComponent(GRAY + "======");

		if (pageIndex < maxPages) {
			next = new TextComponent(" [>>>>] ");
			next.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/pingtop " + (pageIndex + 1)));
		}
		else
			next = new TextComponent(GRAY + "========");

		return new TextComponent(border, previous, next, border);
	}

}
