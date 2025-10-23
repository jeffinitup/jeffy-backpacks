package com.jeffyjamzhd.jeffybackpacks.registry;

import btw.world.util.difficulty.Difficulties;
import btw.world.util.difficulty.DifficultyParam;
import btw.world.util.difficulty.DifficultyProvider;
import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;

public class JBDifficulty {
    public static class BackpackEncumberanceScaling extends DifficultyParam<Integer> {}

    /**
     * Called from BTWAddon#preInitialize
     */
    public static void preRegister() {
        JeffyBackpacks.logInfo("Setting defaults for difficulty params...");
        DifficultyProvider.setDefaultForParameter(BackpackEncumberanceScaling.class, 1);
    }

    /**
     * Called from BTWAddon#initialize
     */
    public static void postRegister() {
        JeffyBackpacks.logInfo("Setting values for difficulty params...");
        Difficulties.RELAXED.modifyParam(BackpackEncumberanceScaling.class, 2);
        Difficulties.CLASSIC.modifyParam(BackpackEncumberanceScaling.class, 4);
    }
}
