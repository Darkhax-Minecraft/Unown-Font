package net.darkhax.unownfont.common;

import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Unown {

    public static final String MOD_ID = "unownfont";
    public static final String MOD_NAME = "Unown Font";
    public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

    public static final Identifier FONT_ID = id("unown");

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}