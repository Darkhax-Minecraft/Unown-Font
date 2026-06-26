package net.darkhax.unownfont.common;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import org.jspecify.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

public class MewMural {

    private static final String[][] NAMES = {
            {"a", "b", "c", "d", "e", "f", "g", "h", "i"},
            {"j", "k", "l", "m", "n", "o", "p", "q", "r"},
            {"s", "t", "u", "v", "w", "x", "y", "z", "special"}
    };

    public static final MuralPiece[][] PIECE_GRID = new MuralPiece[3][9];

    public static final List<MuralPiece> PIECES = Util.make(new LinkedList<>(), list -> {
        for (int y = 0; y < NAMES.length; y++) {
            for (int x = 0; x < NAMES[y].length; x++) {
                final String name = NAMES[y][x];
                final String up = y > 0 ? NAMES[y - 1][x] : null;
                final String down = y < NAMES.length - 1 ? NAMES[y + 1][x] : null;
                final String left = x > 0 ? NAMES[y][x - 1] : null;
                final String right = x < NAMES[y].length - 1 ? NAMES[y][x + 1] : null;
                final MuralPiece piece = new MuralPiece(blockId(name), x, y, blockId(up), blockId(down), blockId(left), blockId(right));
                list.add(piece);
                PIECE_GRID[y][x] = piece;
            }
        }
    });

    public record MuralPiece(ResourceKey<Block> id, int x, int y, @Nullable ResourceKey<Block> up, @Nullable ResourceKey<Block> down, @Nullable ResourceKey<Block> left, @Nullable ResourceKey<Block> right) {
    }

    private static ResourceKey<Block> blockId(String name) {
        return name != null ? ResourceKey.create(Registries.BLOCK, Unown.id("mew_mural_" + name)) : null;
    }
}