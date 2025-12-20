package com.jeffyjamzhd.jeffybackpacks;

import api.BTWAddon;
import com.jeffyjamzhd.jeffybackpacks.registry.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.src.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class JeffyBackpacks extends BTWAddon {
    /**
     * Addon instance
     */
    private static JeffyBackpacks instance;
    /**
     * Addon logger
     */
    private final Logger logger;

    public JeffyBackpacks() {
        super();

        instance = this;
        logger = LogManager.getLogger(this.getModID());
    }

    @Override
    public void preInitialize() {
    }

    @Override
    public void initialize() {
        logInfo("{} Version {} initializing...", this.getName(), this.getVersionString());

        JBPackets.register(this);
        JBItems.register();
        JBTags.register();
        JBDifficulty.postRegister();
        JBRecipes.register();
        JBPenalties.register();
        JBAchievements.register();
        JBSounds.register();

        if (!MinecraftServer.getIsServer()) {
            JBRender.register();
        }

        logInfo("{} initialized!", this.getName());
    }

    /**
     * Logs information in addon logger, formatted with {@code args}
     */
    public static void logInfo(String message, Object... args) {
        if (instance != null)
            instance.logger.info(message, args);
    }

    /**
     * Logs warning in addon logger, formatted with {@code args}
     */
    public static void logWarning(String message, Object... args) {
        if (instance != null)
            instance.logger.warn(message, args);
    }

    /**
     * Logs information in addon logger
     */
    public static void logInfo(String message) {
        logInfo(message, new Object[0]);
    }

    /**
     * Logs warning in addon logger
     */
    public static void logWarning(String message) {
        logWarning(message, new Object[0]);
    }

    /**
     * Gets addon instance
     */
    public static JeffyBackpacks getInstance() {
        return instance;
    }
}