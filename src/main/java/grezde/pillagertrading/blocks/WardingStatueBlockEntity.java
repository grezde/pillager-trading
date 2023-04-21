package grezde.pillagertrading.blocks;

import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.items.PTItems;
import grezde.pillagertrading.util.PTConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WardingStatueBlockEntity extends BlockEntity {

    private int ticksLeft = -1;

    private ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            if(getStackInSlot(0).isEmpty())
                return;
            refuel(getLevel(), getBlockPos());
            setStackInSlot(0, ItemStack.EMPTY);
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(PTItems.LAPIS_CORE.get()) && stack.getCount() == 1;
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();


    public WardingStatueBlockEntity(BlockPos pos, BlockState blockState) {
        super(PTBlockEntities.WARDING_STATUE.get(), pos, blockState);
    }

    @Override
    public void load(CompoundTag tag) {
        this.ticksLeft = tag.getInt("ticks");
        super.saveAdditional(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.putInt("ticks", ticksLeft);
        super.saveAdditional(tag);
    }


    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemStackHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(
                cap != ForgeCapabilities.ITEM_HANDLER
                || side == null
                || side == getBlockState().getValue(WardingStatueBlock.FACING)
                || getBlockState().getValue(WardingStatueBlock.ACTIVE)
        )
            return super.getCapability(cap, side);
        return lazyItemHandler.cast();
    }

    public static void tick(Level level, BlockPos blockPos, BlockState blockState, WardingStatueBlockEntity e) {
        if(level.isClientSide() || e.ticksLeft <= 0)
            return;
        if(e.ticksLeft > 0)
            e.ticksLeft--;
        if(e.ticksLeft == 0) {
            BlockState bs = blockState.setValue(WardingStatueBlock.ACTIVE, false);
            level.setBlock(blockPos, bs, 3);
            e.setBlockState(bs);
        }
    }

    public boolean isActive() {
        return this.ticksLeft > 0;
    }

    public void refuel(Level level, BlockPos pos) {
        level.playSound((Player) null, pos, SoundEvents.IRON_GOLEM_REPAIR, SoundSource.PLAYERS, 0.2F,  2.0F);
        BlockState bs = level.getBlockState(pos).setValue(WardingStatueBlock.ACTIVE, true);
        level.setBlock(pos, bs, 3);
        setBlockState(bs);
        this.ticksLeft = PTConfig.STATUE_COOLDOWN.get();
    }
}
