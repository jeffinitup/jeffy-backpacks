package com.jeffyjamzhd.jeffybackpacks.registry;

import api.achievement.AchievementEvents;
import api.achievement.AchievementProvider;
import api.achievement.AchievementTab;
import net.minecraft.src.Achievement;
import net.minecraft.src.ItemStack;
import net.minecraft.src.ResourceLocation;

public class JBAchievements {
    public static final AchievementTab JEFFY_BACKPACKS;
    public static final Achievement<ItemStack> CRAFT_BUNDLE;
    public static final Achievement<ItemStack> CRAFT_LUNCHBOX;
    public static final Achievement<ItemStack> CRAFT_SATCHEL;
    public static final Achievement<ItemStack> CRAFT_BACKPACK;
    public static final Achievement<ItemStack> CRAFT_FILTER;

    public static void register() {
    }

    private static ResourceLocation loc(String id) {
        return new ResourceLocation("jbp", id);
    }

    static {
        JEFFY_BACKPACKS = new AchievementTab("jeffy_backpacks").setIcon(JBItems.backpack);
        CRAFT_BUNDLE = AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                .name(loc("craft_bundle"))
                .icon(JBItems.bundle)
                .displayLocation(0, 0)
                .triggerCondition(stack -> stack.itemID == JBItems.bundle.itemID)
                .build().registerAchievement(JEFFY_BACKPACKS);
        CRAFT_SATCHEL = AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                .name(loc("craft_satchel"))
                .icon(JBItems.satchel)
                .displayLocation(1, 0)
                .triggerCondition(stack -> stack.itemID == JBItems.satchel.itemID)
                .parents(CRAFT_BUNDLE)
                .build().registerAchievement(JEFFY_BACKPACKS);
        CRAFT_LUNCHBOX = AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                .name(loc("craft_lunchbox"))
                .icon(JBItems.lunchbox)
                .displayLocation(3, 1)
                .triggerCondition(stack -> stack.itemID == JBItems.lunchbox.itemID)
                .parents(CRAFT_SATCHEL)
                .build().registerAchievement(JEFFY_BACKPACKS);
        CRAFT_BACKPACK = AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                .name(loc("craft_backpack"))
                .icon(JBItems.backpack)
                .displayLocation(2, 0)
                .triggerCondition(stack -> stack.itemID == JBItems.backpack.itemID)
                .parents(CRAFT_SATCHEL)
                .build().registerAchievement(JEFFY_BACKPACKS);
        CRAFT_FILTER = AchievementProvider.getBuilder(AchievementEvents.ItemEvent.class)
                .name(loc("craft_filter"))
                .icon(JBItems.filter)
                .displayLocation(3, 0)
                .triggerCondition(stack -> stack.itemID == JBItems.filter.itemID)
                .parents(CRAFT_BACKPACK)
                .build().registerAchievement(JEFFY_BACKPACKS);
    }
}
