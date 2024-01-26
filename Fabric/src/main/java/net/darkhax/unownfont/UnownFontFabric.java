package net.darkhax.unownfont;

import net.fabricmc.api.ModInitializer;

public class UnownFontFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {

        new UnownFontCommon();
    }
}