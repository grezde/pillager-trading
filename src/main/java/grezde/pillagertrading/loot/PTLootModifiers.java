package grezde.pillagertrading.loot;

import com.mojang.serialization.Codec;
import grezde.pillagertrading.PTMod;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootTableIdCondition;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PTLootModifiers {

    public static DeferredRegister<Codec<? extends IGlobalLootModifier>> LOOT_MODIFIERS = DeferredRegister.create(ForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, PTMod.MODID);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_ITEM = LOOT_MODIFIERS.register("add_item", AddItemLootModdifier.CODEC);

    public static final RegistryObject<Codec<? extends IGlobalLootModifier>> ADD_DOUBLING_ITEM = LOOT_MODIFIERS.register("add_doubling_item", AddDoublingItemModdifier.CODEC);

    public static void register(IEventBus modEventBus) {
        LOOT_MODIFIERS.register(modEventBus);
    }

}
