
package jdz.jac.detection.aimbot.aimData;

import static jdz.jac.utils.Messager.plainMessage;
import static org.bukkit.ChatColor.AQUA;
import static org.bukkit.ChatColor.RESET;
import static org.bukkit.ChatColor.YELLOW;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import jdz.jac.JAC;
import jdz.jac.detection.aimbot.neuralNetwork.DataSet;

public class DataIO {
	public static void initFolders() {
		JAC.getInstance().getStorageFolder().mkdirs();
		getFile("captures").mkdirs();
		getFile("categories").mkdirs();
	}

	private static File getFile(String file) {
		return new File(JAC.getInstance().getDataFolder(), file);
	}

	public static void saveCapturedData(String name, DataSeries data) throws Exception {
		FileConfiguration config = new YamlConfiguration();
		File file = getFile("captures" + File.separator + name + ".yml");
		file.createNewFile();
		config.load(file);
		config.set("stddev", data.getAngleSeries());
		config.set("delta-stddev", data.getDeltaStddev());
		config.save(file);
	}

	public static void saveCategory(String category, List<DataSet> samples) throws Exception {
		FileConfiguration config = new YamlConfiguration();
		File file = getFile("categories" + File.separator + category + ".yml");
		if (file.exists())
			file.delete();
		file.createNewFile();
		config.load(file);

		List<Double[]> dumps = new ArrayList<>();
		for (DataSet dataset : samples)
			dumps.add(dataset.getData());

		config.set("category", category);
		config.set("samples." + new Date().toString(), dumps);
		config.save(file);
	}

	public static List<DataSet> getAllCategory() throws Exception {
		List<DataSet> storedSamples = new ArrayList<>();
		File categoryFolder = getFile("categories");
		for (File file : categoryFolder.listFiles()) {
			FileConfiguration config = YamlConfiguration.loadConfiguration(file);
			importSamples(config, storedSamples);
		}
		return storedSamples;
	}

	private static void importSamples(FileConfiguration config, List<DataSet> samples) {
		String category = config.getString("category");
		ConfigurationSection samplesSection = config.getConfigurationSection("samples");

		for (String key : samplesSection.getKeys(false)) {
			@SuppressWarnings("unchecked")
			List<List<Double>> dumps = (List<List<Double>>) samplesSection.getList(key);
			for (List<Double> line : dumps) {
				Double[] dump = new Double[line.size()];
				line.toArray(dump);
				samples.add(new DataSet(category, dump));
			}
		}
	}

	public static void sendInfoToPlayer(Player player) {
		try {
			int categoryNum = getStoredCategoryNum();
			plainMessage(player, AQUA + "  Stored " + categoryNum + (categoryNum != 1 ? " categories" : " category"));
			File cat_folder = getFile("categories");
			for (File file : cat_folder.listFiles())
				sendInfoToPlayer(YamlConfiguration.loadConfiguration(file), player);
		}
		catch (Exception e) {
			e.printStackTrace();
			plainMessage(player, ChatColor.RED + "Something went wrong in fetching category data");
		}
	}

	private static void sendInfoToPlayer(FileConfiguration config, Player player) {
		List<DataSet> samples = new ArrayList<>();
		importSamples(config, samples);
		plainMessage(player, "   Category " + YELLOW + config.getString("category") + RESET + " samples: " + YELLOW
				+ samples.size());
	}

	public static int getStoredCategoryNum() {
		return getFile("categories").listFiles().length;
	}

}
