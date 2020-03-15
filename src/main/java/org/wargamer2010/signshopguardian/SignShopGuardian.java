package org.wargamer2010.signshopguardian;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.wargamer2010.signshop.configuration.SignShopConfig;
import org.wargamer2010.signshop.configuration.configUtil;
import org.wargamer2010.signshopguardian.listeners.SignShopGuardianListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SignShopGuardian extends JavaPlugin {
    private static final int B_STATS_ID = 6770;
    private static final Logger logger = Logger.getLogger("Minecraft");
    private static SignShopGuardian instance = null;
    private static final String metaName = "Guardians";

    // Settings
    private static List<String> EnabledWorlds;
    private static boolean EnableSaveXP = false;

    /**
     * Log given message at given level for SignShopGuardian
     *
     * @param message Message to log
     * @param level   Level to log at
     */
    public static void log(String message, Level level) {
        if (!message.isEmpty())
            logger.log(level, ("[SignShopGuardian] " + message));
    }

    private static void setInstance(SignShopGuardian newInstance) {
        instance = newInstance;
    }

    @Override
    public void onDisable() {
        log("Disabled", Level.INFO);
    }

    public static boolean isNotEnabledForWorld(World world) {
        if (EnabledWorlds.isEmpty())
            return false;
        for (String sWorld : EnabledWorlds)
            if (sWorld.equalsIgnoreCase(world.getName()))
                return false;
        return true;
    }

    /**
     * Gets the key name used in Player Metadata
     *
     * @return key name for meta
     */
    public static String getMetaName() {
        return metaName;
    }

    private void getSettings(FileConfiguration ymlThing) {
        EnabledWorlds = ymlThing.getStringList("EnabledWorlds"); // Empty list if not found
        EnableSaveXP = ymlThing.getBoolean("EnableSaveXP", EnableSaveXP);
    }

    private void createDir() {
        if (!this.getDataFolder().exists()) {
            if (!this.getDataFolder().mkdir()) {
                log("Could not create plugin folder!", Level.SEVERE);
            }
        }
    }

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        if (!pm.isPluginEnabled("SignShop")) {
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
            for (Map.Entry<String, HashMap<String, String>> entry : configUtil.fetchHasmapInHashmap("messages", ymlThing).entrySet()) {
                SignShopConfig.registerMessages(entry.getKey(), entry.getValue());
            }

            getSettings(ymlThing);
        }
        setInstance(this);
        //Enable metrics
        if (SignShopConfig.metricsEnabled()) {
            Metrics metrics = new Metrics(this, B_STATS_ID);
            log("Thank you for enabling metrics!", Level.INFO);
        }
        log("Enabled", Level.INFO);
    }

    /**
     * Gets the instance of SignShopGuardian
     *
     * @return instance
     */
    public static SignShopGuardian getInstance() {
        return instance;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String commandLabel, @NotNull String[] args) {
        String commandName = cmd.getName().toLowerCase();
        if (commandName.equalsIgnoreCase("countguards") || commandName.equalsIgnoreCase("countguardians")
                || commandName.equalsIgnoreCase("getguardians") || commandName.equalsIgnoreCase("guardiansleft"))
            CommandHandler.handleGuardianQuery(sender, args);
        else if (commandName.equalsIgnoreCase("addguardians"))
            return CommandHandler.handleAddGuardians(sender, args);

        return true;
    }

    /**
     * Returns True if XP levels should be saved on player death
     *
     * @return EnableSaveXP setting from config
     */
    public static boolean isEnableSaveXP() {
        return EnableSaveXP;
    }
}
