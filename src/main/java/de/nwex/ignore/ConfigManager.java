package de.nwex.ignore;

import de.nwex.ignore.util.FileUtil;
import java.io.File;
import net.fabricmc.loader.api.FabricLoader;
import org.json.JSONArray;

public class ConfigManager {

    public static void get() {
        File configFile = new File(
            FabricLoader.getInstance().getConfigDirectory() + File.separator + "ignoreplayer" + File.separator + "players.json");

        if (!configFile.exists()) {
            FileUtil.setTextContent(configFile, "[]");
        }

        JSONArray config = new JSONArray(FileUtil.getTextContent(configFile));

        IgnorePlayer.ignored.clear();

        for (int i = 0; i < config.length(); i++) {
            IgnorePlayer.ignored.add(config.getString(i));
        }
    }

    public static void set() {
        JSONArray out = new JSONArray();

        IgnorePlayer.ignored.forEach(out::put);

        FileUtil.setTextContent(
            new File(FabricLoader.getInstance().getConfigDirectory() + File.separator + "ignoreplayer" + File.separator + "players.json"),
            out.toString());
    }
}
