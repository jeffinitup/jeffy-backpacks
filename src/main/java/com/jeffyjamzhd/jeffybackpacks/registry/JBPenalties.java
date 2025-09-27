package com.jeffyjamzhd.jeffybackpacks.registry;

import btw.util.status.StatusCategory;
import btw.util.status.StatusEffect;
import btw.util.status.StatusEffectBuilder;
import btw.world.util.difficulty.Difficulty;
import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import net.minecraft.src.DamageSource;

public class JBPenalties {
    public static DamageSource DAMAGE_CRUSHED = new DamageSource("crushed")
            .setDamageBypassesArmor();

    public static StatusEffect WEIGHTED;
    public static StatusEffect ENCUMBERED;
    public static StatusEffect CRUSHED;

    public static void register() {
        JeffyBackpacks.logInfo("Registering status effects");

        WEIGHTED = buildEncumbranceStatus(1, "weighted", 0.8F).build();
        ENCUMBERED = buildEncumbranceStatus(2, "encumbered", 0.45F).build();
        CRUSHED = buildEncumbranceStatus(3, "crushed", 0.1F).build();
    }

    private static StatusEffectBuilder buildEncumbranceStatus(int level, String name, float effectiveness) {
        StatusEffectBuilder effect = (new StatusEffectBuilder(level, JBPStatusCategory.BACKPACK_STATUS))
                .setEffectivenessMultiplier(effectiveness)
                .setAffectsMovement()
                .setPreventsSprinting()
                .jbp$setFallDamageMultiplier(1.0F + (.5F * level) + (level > 2 ? .5F : 0F))
                .jbp$setHungerMultiplier(1.0F + (.5F * level))
                .setUnlocalizedName(JBPStatusCategory.BACKPACK_STATUS.getName(), name);

        if (level > 1) {
            effect.setPreventsJumping();
            effect.setAffectsMiningSpeed();
        }

        if (level > 2) {
            effect.jbp$setDamageOverTime(DAMAGE_CRUSHED, 1, 40);
        }

        effect.setEvaluator(player -> {
            if (!player.capabilities.isCreativeMode) {
                // Get data
                int count = player.jbp$getBackpackItemCount();
                Difficulty diff = player.getEntityWorld().getDifficulty();

                // Scale based on difficulty
                if (!diff.hasHardcoreSpawn()) {
                    // Classic
                    count = Math.max(0, count - 27);
                } else {
                    if (diff.allowsPlacingBlocksInAir()) {
                        // Relaxed
                        count = Math.max(0, count - 9);
                    }
                }

                return count >= 9 * level;
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
