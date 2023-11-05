package com.github.nerjalnosk.advancement_cascade.forge;

import com.github.nerjalnosk.advancement_cascade.AdvancementCascade;
import net.minecraftforge.fml.common.Mod;

@Mod(AdvancementCascade.MOD_ID)
public class AdvancementCascadeForge {
    public AdvancementCascadeForge() {
        AdvancementCascade.init();
    }
}