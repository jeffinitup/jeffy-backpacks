package com.jeffyjamzhd.jeffybackpacks;

import btw.BTWAddon;
import com.jeffyjamzhd.jeffybackpacks.registry.*;
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
    public void initialize() {
        logInfo("{} Version {} initializing...", this.getName(), this.getVersionString());

        JBPackets.register(this);
        JBItems.register();
        JBTags.register();
        JBRecipes.register();
        JBPenalties.register();
        JBAchievements.register();
        JBSounds.register();

        logInfo("{} initialized!", this.getName());
    }

    /**
     * Logs information in addon logger, formatted with {@code args}
     */
    public static void logInfo(String message, Object... args) {
        instance.logger.info(message, args);
    }

    /**
     * Logs information in addon logger
     */
    public static void logInfo(String message) {
        logInfo(message, new Object[0]);
    }

    /**
     * Gets addon instance
     */
    public static JeffyBackpacks getInstance() {
        return instance;
    }
}