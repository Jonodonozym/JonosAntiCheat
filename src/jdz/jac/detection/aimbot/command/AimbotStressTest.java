
package jdz.jac.detection.aimbot.command;

import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import jdz.bukkitUtils.commands.SubCommand;
import jdz.bukkitUtils.commands.annotations.CommandLabel;
import jdz.bukkitUtils.commands.annotations.CommandMethod;
import jdz.bukkitUtils.commands.annotations.CommandOpOnly;
import jdz.bukkitUtils.commands.annotations.CommandUsage;
import jdz.jac.JAC;
import jdz.jac.detection.aimbot.AimbotClassifier;
import jdz.jac.detection.aimbot.aimData.DataManager;
import jdz.jac.detection.aimbot.aimData.DataSeries;
import jdz.jac.detection.aimbot.neuralNetwork.PredictResult;

@CommandLabel("stresstest")
@CommandOpOnly
@CommandUsage("[numSimultaneousChecks]")
public class AimbotStressTest extends SubCommand {

	@CommandMethod
	public void test(Player sender, int checks) {
		if (checks < 1) {
			sender.sendMessage("[JAC] I think you should run more checks than that. This is a stress test, after all!");
		}

		if (DataManager.isRecording(sender)) {
			sender.sendMessage("[JAC] You are already being measured, please wait about 10 seconds");
			return;
		}

		DataManager.startRecording(sender);
		sender.sendMessage("[JAC] Good to go, start hitting something for 15 seconds!");
		Bukkit.getScheduler().runTaskLaterAsynchronously(JAC.getInstance(), () -> {
			DataSeries data = DataManager.stopRecording(sender);

			long startTime = System.currentTimeMillis();
			sender.sendMessage("[JAC] starting CPU usage:" + getProcessCpuLoad());

			PredictResult result = null;
			for (int i = 0; i < checks; i++) {
				result = AimbotClassifier.classify(data);
				if (i == checks / 2)
					sender.sendMessage("[JAC] mid-way CPU usage:" + getProcessCpuLoad());
			}

			sender.sendMessage("[JAC] end CPU usage:" + getProcessCpuLoad());
			sender.sendMessage("[JAC] time for " + checks + " classifications: "
					+ (System.currentTimeMillis() - startTime) + "ms");

			sender.sendMessage("[JAC] In case you are curious, you were classed as " + result.getBestMatched());
		}, 20L * 15);
	}

	private static double getProcessCpuLoad() {
		try {
			MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
			AttributeList list = mbs.getAttributes(name, new String[] { "ProcessCpuLoad" });
			if (list.isEmpty())
				return Double.NaN;

			Attribute att = (Attribute) list.get(0);
			Double value = (Double) att.getValue();

			// usually takes a couple of seconds before we get real values
			if (value == -1.0)
				return Double.NaN;
			// returns a percentage value with 1 decimal point precision
			return ((int) (value * 1000) / 10.0);
		}
		catch (Exception e) {
			e.printStackTrace();
			return Double.NaN;
		}

	}

}
