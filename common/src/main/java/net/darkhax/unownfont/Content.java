package net.darkhax.unownfont;

import net.darkhax.bookshelf.api.entity.merchant.trade.VillagerSells;
import net.darkhax.bookshelf.api.registry.IRegistryObject;
import net.darkhax.bookshelf.api.registry.RegistryDataProvider;
import net.darkhax.unownfont.item.UnownFontPattern;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;

import java.util.Arrays;

public class Content extends RegistryDataProvider {

    private static final String[] PATTERN_NAMES = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "question", "exclamation"};

    public Content() {

        super(Constants.MOD_ID);

        Arrays.stream(PATTERN_NAMES).forEach(this::createPattern);
        final IRegistryObject<Item> patternStencil = this.items.add(UnownFontPattern::new, "unown_pattern");
        this.trades.addRareWanderingTrade(VillagerSells.create(patternStencil, 4, 5, 10, 0.05f));
    }

    private void createPattern(String name) {

        final String id = "unown_" + name;
        this.bannerPatterns.add(() -> new BannerPattern(id), id);
    }
}