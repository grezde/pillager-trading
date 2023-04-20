package grezde.pillagertrading.blocks;

import grezde.pillagertrading.PTMod;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PTBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PTMod.MODID);

    public static final RegistryObject<BlockEntityType<WardingStatueBlockEntity>> WARDING_STATUE = BLOCK_ENTITIES.register("warding_statue", () -> BlockEntityType.Builder.of(
            WardingStatueBlockEntity::new,
            PTBlocks.WARDING_STATUE_BLOCK.get()
    ).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }

}
