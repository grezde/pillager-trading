package grezde.pillagertrading.blocks;

import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.items.PTItems;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WardingStatueBlock extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LEFT_ARM = BooleanProperty.create("leftarm");
    public static final BooleanProperty RIGHT_ARM = BooleanProperty.create("rightarm");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    protected static final VoxelShape SHAPE_SOUTH = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 20.0D, 9.0D);
    protected static final VoxelShape SHAPE_EAST = Block.box(1.0D, 0.0D, 1.0D, 9.0D, 20.0D, 15.0D);
    protected static final VoxelShape SHAPE_WEST = Block.box(7.0D, 0.0D, 1.0D, 15.0D, 20.0D, 15.0D);
    protected static final VoxelShape SHAPE_NORTH = Block.box(1.0D, 0.0D, 7.0D, 15.0D, 20.0D, 15.0D);

    public WardingStatueBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return switch (blockState.getValue(FACING)) {
            case SOUTH -> SHAPE_SOUTH;
            case WEST-> SHAPE_WEST;
            case EAST -> SHAPE_EAST;
            case NORTH -> SHAPE_NORTH;
            default -> super.getShape(blockState, getter, pos, context);
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        //return getShape(state, blockGetter, pos, context);
        return getShape(state, blockGetter, pos, context);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide) {
            if(player.getItemInHand(hand).isEmpty()) {
                if(player.isCrouching())
                    player.displayClientMessage(
                            state.getValue(LEFT_ARM) ? Component.translatable("gui.pillagertrading.statue.patrol_off") : Component.translatable("gui.pillagertrading.statue.patrol_on"), true);
                else
                    player.displayClientMessage(state.getValue(RIGHT_ARM) ? Component.translatable("gui.pillagertrading.statue.wanderer_off") : Component.translatable("gui.pillagertrading.statue.wanderer_on"), true);
            }
        }
        else if(level.getBlockEntity(pos) instanceof WardingStatueBlockEntity blockEntity && level instanceof ServerLevel sl) {
            if(player.getItemInHand(hand).is(PTItems.EMERALD_CORE.get())) {
                // FOR DEBUG PURPOSES
                PTMod.LOGGER.info("Tried spawning wandering trader");
                WanderingTrader trader = EntityType.WANDERING_TRADER.spawn(sl, null, null, null, pos.east(), MobSpawnType.EVENT, true, false);
            }
            else if(player.getItemInHand(hand).is(PTItems.LAPIS_CORE.get()) && !state.getValue(ACTIVE)) {
                if(!player.getAbilities().instabuild)
                    player.getItemInHand(hand).shrink(1);
                blockEntity.refuel(level, pos);
            }
            else if(player.getItemInHand(hand).isEmpty()) {
                if(player.isCrouching())
                    level.setBlock(pos, state.setValue(LEFT_ARM, !state.getValue(LEFT_ARM)), 3);
                else
                    level.setBlock(pos, state.setValue(RIGHT_ARM, !state.getValue(RIGHT_ARM)), 3);
            }
            else
                return InteractionResult.PASS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        for(Direction dir : context.getNearestLookingDirections())
            if(dir != Direction.UP && dir != Direction.DOWN)
                return defaultBlockState().setValue(FACING, dir.getOpposite()).setValue(LEFT_ARM, false).setValue(RIGHT_ARM, false).setValue(ACTIVE, false);
        return defaultBlockState();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LEFT_ARM, RIGHT_ARM, ACTIVE);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new WardingStatueBlockEntity(pos, blockState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> beType) {
        return createTickerHelper(beType, PTBlockEntities.WARDING_STATUE.get(), WardingStatueBlockEntity::tick);
    }
}
