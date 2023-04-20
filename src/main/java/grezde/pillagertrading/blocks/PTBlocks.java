package grezde.pillagertrading.blocks;

import grezde.pillagertrading.PTMod;
import grezde.pillagertrading.items.PTItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class PTBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, PTMod.MODID);

    public static final RegistryObject<Block> GILDED_STONE_BRICKS = registerSimpleBlock("gilded_stone_bricks", Blocks.STONE_BRICKS);
    public static final RegistryObject<Block> GILDED_DEEPSLATE_BRICKS = registerSimpleBlock("gilded_deepslate_bricks", Blocks.DEEPSLATE_BRICKS);
    public static final RegistryObject<Block> WARDING_STATUE_BLOCK = registerBlock("warding_statue", () -> new WardingStatueBlock(BlockBehaviour.Properties.copy(Blocks.CUT_COPPER_SLAB).noOcclusion()));


    private static RegistryObject<Block> registerBlock(String name, Supplier<? extends Block> supplier) {
        RegistryObject<Block> block = BLOCKS.register(name, supplier);
        PTItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    private static RegistryObject<Block> registerSimpleBlock(String name, Block copyPropertiesFrom) {
        return registerBlock(name, () -> new Block(BlockBehaviour.Properties.copy(copyPropertiesFrom)));
    }


    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }

}
