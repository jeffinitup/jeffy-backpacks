package com.jeffyjamzhd.jeffybackpacks.registry;

import com.jeffyjamzhd.jeffybackpacks.JeffyBackpacks;
import com.jeffyjamzhd.jeffybackpacks.render.model.ModelBackpack;
import com.jeffyjamzhd.jeffybackpacks.render.model.ModelSatchel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class JBRender {
    public static void register() {
        JeffyBackpacks.logInfo("Registering backpack renderers...");

        JBItems.satchel.setCustomModel(new ModelSatchel());
        JBItems.backpack.setCustomModel(new ModelBackpack());
    }
}
