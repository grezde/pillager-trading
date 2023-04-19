package grezde.pillagertrading.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import grezde.pillagertrading.PTMod;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AddDoublingItemModdifier extends LootModifier {

    // magic?
    public static final Supplier<Codec<AddDoublingItemModdifier>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst -> codecStart(inst)
                    .and(ForgeRegistries.ITEMS.getCodec().fieldOf("item").forGetter(m -> m.item))
                    .and(Codec.FLOAT.fieldOf("chance").forGetter(m -> m.chance))
                    .and(Codec.FLOAT.fieldOf("doublingChance").forGetter(m -> m.doublingChance))
                    .apply(inst, AddDoublingItemModdifier::new)));

    private final float doublingChance;
    private final float chance;
    private final Item item;

    protected AddDoublingItemModdifier(LootItemCondition[] conditionsIn, Item item, float chance, float doublingChance) {
        super(conditionsIn);
        this.item = item;
        this.chance = chance;
        this.doublingChance = doublingChance;
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if(context.getRandom().nextFloat() < 0.8f) {
            generatedLoot.add(new ItemStack(item, 1));
            if(context.getRandom().nextFloat() < 0.5f)
                generatedLoot.add(new ItemStack(item, 1));
        }
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
