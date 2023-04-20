package grezde.pillagertrading.items;

import grezde.pillagertrading.PTMod;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class PTItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, PTMod.MODID);

    public static final RegistryObject<Item> ILLAGER_MANUSCRIPT = registerItem("illager_manuscript", () -> new IllagerManuscriptItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MATERIALS).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> ILLAGER_ORDER = registerItem("illager_order", () -> new IllagerOrderItem(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final RegistryObject<Item> LAPIS_CORE = registerItem("lapis_core", new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_MATERIALS));
    public static final RegistryObject<Item> EMERALD_CORE = registerItem("emerald_core", new Item.Properties().stacksTo(16).tab(CreativeModeTab.TAB_MATERIALS));

    public static RegistryObject<Item> registerItem(String location, Supplier<Item> itemSupplier) {
        return ITEMS.register(location, itemSupplier);
    }

    public static RegistryObject<Item> registerItem(String location, Item.Properties itemProperites) {
        return registerItem(location, () -> new Item(itemProperites));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }

}
