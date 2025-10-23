package com.jeffyjamzhd.jeffybackpacks.registry;

import btw.util.status.StatusCategory;
import btw.util.status.StatusEffect;
import btw.util.status.StatusEffectBuilder;
import btw.world.util.difficulty.Difficulty;
import btw.world.util.difficulty.DifficultyParam;
import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.mixin.invoker.StatusEffectBuilderInvoker;
import net.minecraft.src.DamageSource;
import net.minecraft.src.World;

import java.util.Optional;

public class JBPenalties {
    public static DamageSource DAMAGE_CRUSHED = new DamageSource("crushed")
            .setDamageBypassesArmor();

    public static StatusEffect WEIGHTED;
    public static StatusEffect ENCUMBERED;
    public static StatusEffect CRUSHED;

    public static void register() {
        JeffyBackpacks.logInfo("Registering status effects");

        WEIGHTED = buildEncumbranceStatus(1, "weighted", 0.8F).build();
        ENCUMBERED = buildEncumbranceStatus(2, "encumbered", 0.6F).build();
        CRUSHED = buildEncumbranceStatus(3, "crushed", 0.1F).build();
    }

    private static StatusEffectBuilder buildEncumbranceStatus(int level, String name, float effectiveness) {
        StatusEffectBuilder effect = (StatusEffectBuilderInvoker.create(level, JBPStatusCategory.BACKPACK_STATUS))
                .setEffectivenessMultiplier(effectiveness)
                .setAffectsMovement()
                .jbp$setFallDamageMultiplier(1.0F + (.5F * level) + (level > 2 ? .5F : 0F))
                .jbp$setHungerMultiplier(1.0F + (.5F * level) + (level > 1 ? 1F : 0F))
                .setUnlocalizedName(JBPStatusCategory.BACKPACK_STATUS.getName(), name);

        if (level > 1)
            effect.setPreventsSprinting()
                    .setAffectsMiningSpeed();

        if (level > 2)
            effect
                    .setPreventsJumping()
                    .jbp$setDamageOverTime(DAMAGE_CRUSHED, 1, 80);

        effect.setEvaluator(player -> {
            if (!player.capabilities.isCreativeMode) {
                // Get data
                int count = player.jbp$getBackpackItemCount();
                World world = player.getEntityWorld();
                int scaling = world.getDifficultyParameter(JBDifficulty.BackpackEncumberanceScaling.class);

                return count >= (9 * scaling) * level;
            }
            return false;
        });
        return effect;
    }

    public enum JBPStatusCategory implements StatusCategory {
        BACKPACK_STATUS("backpack");

        private String name;

        JBPStatusCategory(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }
}
