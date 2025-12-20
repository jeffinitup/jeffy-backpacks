package com.jeffyjamzhd.jeffybackpacks.registry;

import api.world.difficulty.DifficultyParam;
import api.world.difficulty.DifficultyProvider;
import btw.world.BTWDifficulties;
import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;

public class JBDifficulty {
    public static class BackpackEncumberanceScaling extends DifficultyParam<Integer> {}

    /**
     * Called from BTWAddon#preInitialize
     */
    public static void preRegister() {
        DifficultyProvider.setDefaultForParameter(BackpackEncumberanceScaling.class, 1);
    }

    /**
     * Called from BTWAddon#initialize
     */
    public static void postRegister() {
        BTWDifficulties.RELAXED.modifyParam(BackpackEncumberanceScaling.class, 2);
        BTWDifficulties.CLASSIC.modifyParam(BackpackEncumberanceScaling.class, 4);
    }
}
