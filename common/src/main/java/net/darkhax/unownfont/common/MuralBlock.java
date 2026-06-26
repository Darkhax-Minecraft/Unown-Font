package net.darkhax.unownfont.common;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Util;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

public class MuralBlock extends Block {

    private static final Map<Direction, VoxelShape> SHAPES = Shapes.rotateHorizontal(Block.box(0, 0, 0, 16, 16, 14));

    private static final EnumMap<Direction, Direction> LEFT = Util.make(new EnumMap<>(Direction.class), m -> {
        m.put(Direction.NORTH, Direction.WEST);
        m.put(Direction.WEST, Direction.SOUTH);
        m.put(Direction.SOUTH, Direction.EAST);
        m.put(Direction.EAST, Direction.NORTH);
    });

    private static final EnumMap<Direction, Direction> RIGHT = Util.make(new EnumMap<>(Direction.class), m -> {
        m.put(Direction.NORTH, Direction.EAST);
        m.put(Direction.EAST, Direction.SOUTH);
        m.put(Direction.SOUTH, Direction.WEST);
        m.put(Direction.WEST, Direction.NORTH);
    });

    private final MewMural.MuralPiece piece;

    public MuralBlock(Properties properties, MewMural.MuralPiece piece) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
        this.piece = piece;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    protected VoxelShape getOcclusionShape(BlockState state) {
        return SHAPES.get(state.getValue(BlockStateProperties.HORIZONTAL_FACING));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        final BlockState placedState = super.getStateForPlacement(context);
        if (placedState != null) {
            for (final Direction facing : context.getNearestLookingDirections()) {
                if (facing.getAxis().isHorizontal()) {
                    return placedState.setValue(BlockStateProperties.HORIZONTAL_FACING, facing);
                }
            }
        }
        return placedState;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, rot.rotate(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(BlockStateProperties.HORIZONTAL_FACING, mirror.mirror(state.getValue(BlockStateProperties.HORIZONTAL_FACING)));
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity by, ItemStack itemStack) {
        if (by instanceof ServerPlayer player) {
            final Direction placedDirection = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (this.isCorrect(level, pos, state, placedDirection)) {
                level.levelEvent(3005, pos, 0);
                final BlockPos originPos = getOrigin(pos, placedDirection);
                final Direction right = RIGHT.get(placedDirection);
                if (isCompleted(level, originPos, placedDirection, right) && awardAdvancement(player, Content.ADVANCEMENT_ID)) {
                    final BlockPos.MutableBlockPos posCursor = originPos.mutable();
                    for (int y = 0; y < MewMural.PIECE_GRID.length; y++) {
                        for (int x = 0; x < MewMural.PIECE_GRID[y].length; x++) {
                            posCursor.set(originPos).move(Direction.DOWN, y).move(right, x);
                            level.levelEvent(3005, posCursor, 0);
                        }
                    }
                }
            }
        }
    }

    private boolean isCorrect(Level level, BlockPos origin, BlockState originState, Direction placedDirection) {
        return (isExpectedState(level.getBlockState(origin.above()), this.piece.up(), placedDirection)) || (isExpectedState(level.getBlockState(origin.below()), this.piece.down(), placedDirection)) || (isExpectedState(level.getBlockState(origin.relative(LEFT.get(placedDirection))), this.piece.left(), placedDirection)) || (isExpectedState(level.getBlockState(origin.relative(RIGHT.get(placedDirection))), this.piece.right(), placedDirection));
    }

    private boolean isExpectedState(BlockState state, ResourceKey<Block> expected, Direction direction) {
        return expected != null && state.is(expected) && state.getValue(BlockStateProperties.HORIZONTAL_FACING) == direction;
    }

    private BlockPos getOrigin(BlockPos pos, Direction placedDirection) {
        return pos.relative(Direction.UP, this.piece.y()).relative(LEFT.get(placedDirection), this.piece.x());
    }

    private static boolean isCompleted(Level level, BlockPos originPos, Direction expectedFacing, Direction right) {
        final BlockPos.MutableBlockPos posCursor = originPos.mutable();
        for (int y = 0; y < MewMural.PIECE_GRID.length; y++) {
            for (int x = 0; x < MewMural.PIECE_GRID[y].length; x++) {
                posCursor.set(originPos).move(Direction.DOWN, y).move(right, x);
                final MewMural.MuralPiece expectedPiece = MewMural.PIECE_GRID[y][x];
                final BlockState state = level.getBlockState(posCursor);
                if (!state.is(expectedPiece.id()) || !state.hasProperty(BlockStateProperties.HORIZONTAL_FACING) || state.getValue(BlockStateProperties.HORIZONTAL_FACING) != expectedFacing) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean awardAdvancement(ServerPlayer player, Identifier advancementId) {
        final AdvancementHolder toGrant = player.level().getServer().getAdvancements().get(advancementId);
        if (toGrant != null) {
            final AdvancementProgress progress = player.getAdvancements().getOrStartProgress(toGrant);
            if (!progress.isDone()) {
                for (String criteria : progress.getRemainingCriteria()) {
                    player.getAdvancements().award(toGrant, criteria);
                }
                return true;
            }
        }
        return false;
    }
}