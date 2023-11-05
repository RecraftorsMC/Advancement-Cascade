package com.github.nerjalnosk.advancement_cascade.fabric;

import com.github.nerjalnosk.advancement_cascade.AdvancementCascade;
import net.fabricmc.api.ModInitializer;

public class AdvancementCascadeFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AdvancementCascade.init();
    }
}