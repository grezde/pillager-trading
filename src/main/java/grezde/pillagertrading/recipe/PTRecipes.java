package grezde.pillagertrading.recipe;

import grezde.pillagertrading.PTMod;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PTRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, PTMod.MODID);

    public static final RegistryObject<RecipeSerializer<PillagerTradingRecipe>> PILLAGER_TRADING_S = SERIALIZERS.register(PillagerTradingRecipe.Type.ID, () -> PillagerTradingRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }

}
