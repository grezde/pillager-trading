package grezde.pillagertrading.blocks;

import grezde.pillagertrading.items.PTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.MinecartFurnace;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class WardinStatueBlock extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty LEFT_ARM = BooleanProperty.create("leftarm");
    public static final BooleanProperty RIGHT_ARM = BooleanProperty.create("rightarm");
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    public WardinStatueBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return super.getShape(p_60555_, p_60556_, p_60557_, p_60558_);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        //return getShape(state, blockGetter, pos, context);
        return super.getCollisionShape(state, blockGetter, pos, context);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(level.isClientSide) {
            if(player.getItemInHand(hand).is(PTItems.LAPIS_CORE.get())) {
                player.playSound(SoundEvents.IRON_GOLEM_REPAIR);
            }
            else if(player.getItemInHand(hand).isEmpty()) {
                if(player.isCrouching())
                    player.displayClientMessage(
                            state.getValue(LEFT_ARM) ? Component.translatable("gui.pillagertrading.statue.patrol_off") : Component.translatable("gui.pillagertrading.statue.patrol_on"), true);
                else
                    player.displayClientMessage(state.getValue(RIGHT_ARM) ? Component.translatable("gui.pillagertrading.statue.wanderer_off") : Component.translatable("gui.pillagertrading.statue.wanderer_on"), true);
            }
        }
        else {
            if(player.getItemInHand(hand).is(PTItems.LAPIS_CORE.get())) {
                level.setBlock(pos, state.setValue(ACTIVE, !state.getValue(ACTIVE)), 3);
                if(!player.getAbilities().instabuild)
                    player.getItemInHand(hand).shrink(1);
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
}
