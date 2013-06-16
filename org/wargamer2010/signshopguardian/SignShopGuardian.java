package org.wargamer2010.signshopguardian;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.configuration.configUtil;
import org.wargamer2010.signshop.metrics.setupMetrics;
import org.wargamer2010.signshopguardian.listeners.SignShopGuardianListener;

public class SignShopGuardian extends JavaPlugin {
    private static final Logger logger = Logger.getLogger("Minecraft");
    private static SignShopGuardian instance = null;
    private static final String metaName = "Guardians";

    /**
     * Log given message at given level for SignShopGuardian
     * @param message Message to log
     * @param level Level to log at
     */
    public static void log(String message, Level level) {
        if(!message.isEmpty())
            logger.log(level,("[SignShopGuardian] " + message));
    }

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if(!pm.isPluginEnabled("SignShop")) {
            log("SignShop is not loaded, can not continue.", Level.SEVERE);
            pm.disablePlugin(this);
            return;
        }
        pm.registerEvents(new SignShopGuardianListener(), this);
        createDir();

        String filename = "config.yml";
        FileConfiguration ymlThing = configUtil.loadYMLFromPluginFolder(this, filename);
        if(ymlThing != null) {
            configUtil.loadYMLFromJar(this, SignShopGuardian.class, ymlThing, filename);

            SignShopConfig.setupOperations(configUtil.fetchStringStringHashMap("signs", ymlThing), "org.wargamer2010.signshopguardian.operations");
            SignShopConfig.registerErrorMessages(configUtil.fetchStringStringHashMap("errors", ymlThing));
            for(Map.Entry<String, HashMap<String, String>> entry : configUtil.fetchHasmapInHashmap("messages", ymlThing).entrySet()) {
                SignShopConfig.registerMessages(entry.getKey(), entry.getValue());
            }
        }

        setupMetrics metrics = new setupMetrics(this);
        if(!metrics.isOptOut()) {
            if(metrics.setup())
                log("Succesfully started Metrics, see http://mcstats.org for more information.", Level.INFO);
            else
                log("Could not start Metrics, see http://mcstats.org for more information.", Level.INFO);
        }

        setInstance(this);
        log("Enabled", Level.INFO);
    }

    @Override
    public void onDisable() {
        log("Disabled", Level.INFO);
    }

    /**
     * Gets the key name used in Player Metadata
     * @return key name for meta
     */
    public static String getMetaName() {
        return metaName;
    }

    private void createDir() {
        if(!this.getDataFolder().exists()) {
            if(!this.getDataFolder().mkdir()) {
                log("Could not create plugin folder!", Level.SEVERE);
            }
        }
    }

    private static void setInstance(SignShopGuardian newinstance) {
        instance = newinstance;
    }

    /**
     * Gets the instance of SignShopGuardian
     * @return instance
     */
    public static SignShopGuardian getInstance() {
        return instance;
    }
}
