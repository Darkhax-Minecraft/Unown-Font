package net.darkhax.unownfont.common;

import net.darkhax.bookshelf.common.api.registry.ContentProvider;
import net.darkhax.bookshelf.common.impl.registry.adapter.BlockRegistryAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.CreativeModeTabAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.ItemRegistryAdapter;
import net.darkhax.bookshelf.common.impl.registry.adapter.PotPatternAdapter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.UnaryOperator;

public class Content implements ContentProvider {

    private static final String[] CHARACTERS = {
            "a", "b", "c", "d", "e", "f", "g",
            "h", "i", "j", "k", "l", "m", "n",
            "o", "p", "q", "r", "s", "t", "u",
            "v", "w", "x", "y", "z", "question", "exclamation"
    };

    public static final Identifier BANNER_PATTERN_TAG = Unown.id("pattern_item/unown");
    public static final TagKey<Block> SLATE_TAG = TagKey.create(Registries.BLOCK, Unown.id("mysterious_slate"));
    public static final Identifier ADVANCEMENT_ID = Unown.id("adventure/complete_the_mural");

    @Override
    public void defineItems(ItemRegistryAdapter registry) {
        registry.addSimple("unown_pattern", props -> props.stacksTo(1).rarity(Rarity.UNCOMMON).delayedComponent(DataComponents.PROVIDES_BANNER_PATTERNS, ctx -> ctx.getOrThrow(TagKey.create(Registries.BANNER_PATTERN, BANNER_PATTERN_TAG))));
        for (String name : CHARACTERS) {
            registry.addSimple(name + "_pottery_sherd", UnaryOperator.identity());
        }
    }

    @Override
    public void defineBlocks(BlockRegistryAdapter registry) {
        final UnaryOperator<BlockBehaviour.Properties> muralProps = p -> p.mapColor(MapColor.WARPED_NYLIUM).requiresCorrectToolForDrops().strength(3f, 6f).instrument(NoteBlockInstrument.TRUMPET_OXIDIZED).sound(SoundType.COPPER);
        for (MewMural.MuralPiece muralPiece : MewMural.PIECES) {
            registry.addPlaceable(muralPiece.id().identifier().getPath(), p -> new MuralBlock(p, muralPiece), muralProps);
        }
    }

    @Override
    public void definePotPatterns(PotPatternAdapter registry) {
        for (String name : CHARACTERS) {
            registry.addWithItem(name + "_pottery_pattern", BuiltInRegistries.ITEM.getValue(Unown.id(name + "_pottery_sherd")));
        }
    }

    @Override
    public void defineCreativeTabs(CreativeModeTabAdapter registry) {
        registry.add("tab", () -> BuiltInRegistries.ITEM.getValue(Unown.id("unown_pattern")).getDefaultInstance(), (params, builder) -> {
            builder.accept(BuiltInRegistries.ITEM.getValue(Unown.id("unown_pattern")));
            for (MewMural.MuralPiece muralPiece : MewMural.PIECES) {
                builder.accept(BuiltInRegistries.ITEM.getValue(muralPiece.id().identifier()));
            }
            for (String name : CHARACTERS) {
                builder.accept(BuiltInRegistries.ITEM.getValue(Unown.id(name + "_pottery_sherd")));
            }
        });
    }

    @Override
    public String namespace() {
        return Unown.MOD_ID;
    }

}