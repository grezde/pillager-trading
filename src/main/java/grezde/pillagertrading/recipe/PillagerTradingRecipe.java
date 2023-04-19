package grezde.pillagertrading.recipe;

import com.google.gson.JsonObject;
import grezde.pillagertrading.PTMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PillagerTradingRecipe implements Recipe<SimpleContainer>, ITredingScreenEntry {

    public static List<PillagerTradingRecipe> getAllRecipes(Level level) {
        return level.getRecipeManager().getRecipes().stream().filter(recipe -> recipe.getType() == Type.INSTANCE).map(recipe -> (PillagerTradingRecipe)recipe).toList();
    }

    private final ResourceLocation id;
    private final ItemStack output;
    private final ItemStack input;

    public PillagerTradingRecipe(ResourceLocation id, ItemStack output, ItemStack input) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    @Override
    public boolean matches(SimpleContainer container, Level level) {
        if(level.isClientSide())
            return false;

        return Ingredient.of(input).test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(SimpleContainer container) {
        return null;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return output.copy();
    }

    public ItemStack getInput() { return input; }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }



    @Override
    public ItemStack getTradingInitialFirstItem() {
        return input;
    }

    @Override
    public ItemStack getTradingFirstItem() {
        return input;
    }

    @Override
    public ItemStack getTradingSecondItem() {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getTradingResult() {
        return output;
    }

    @Override
    public boolean isTradeCrossed() {
        return false;
    }

    public static class Type implements RecipeType<PillagerTradingRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "pillager_trading";
    }

    public static class Serializer implements RecipeSerializer<PillagerTradingRecipe> {

        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(PTMod.MODID, Type.ID);

        @Override
        public PillagerTradingRecipe fromJson(ResourceLocation resourceLocation, JsonObject jsonObject) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "output"));
            ItemStack input = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(jsonObject, "input"));
            return new PillagerTradingRecipe(resourceLocation, output, input);
        }

        @Override
        public @Nullable PillagerTradingRecipe fromNetwork(ResourceLocation resourceLocation, FriendlyByteBuf buf) {
            return new PillagerTradingRecipe(resourceLocation, buf.readItem(), buf.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, PillagerTradingRecipe recipe) {
            buf.writeItemStack(recipe.output, true);
            buf.writeItemStack(recipe.input, true);
        }
    }
}
