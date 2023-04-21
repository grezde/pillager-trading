package grezde.pillagertrading.util;

import grezde.pillagertrading.items.PTItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class MiscUtils {

    public static boolean playerGive(Player p, ItemStack stack) {
        // copied from the give command
        ServerPlayer player = (ServerPlayer) p;
        boolean flag = p.getInventory().add(stack);
        if (flag && stack.isEmpty()) {
            player.level.playSound((Player) null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.containerMenu.broadcastChanges();
            return true;
        }
        ItemEntity itementity = player.drop(stack, false);
        if (itementity != null)
            itementity.setOwner(player.getUUID());
        return false;
    }

    public static boolean villagerHasAI(Villager villager) {
        // better way to check for this?
        return villager.goalSelector.getAvailableGoals().stream().anyMatch((goal) -> goal.getGoal() instanceof TemptGoal);

    }

    public static void villagerAddAI(Villager villager) {
        // TODO: add villagers scared of illager order
        villager.goalSelector.addGoal(2, new TemptGoal(villager, 0.6, Ingredient.of(PTItems.EMERALD_CORE.get()), false));
    }

    public static Stream<ChunkAccess> getChunksInRange(LevelAccessor level, BlockPos pos, int range) {
        List<ChunkAccess> chunks = new ArrayList<>();
        ChunkPos source = level.getChunk(pos).getPos();
        for(int i=source.x - range; i<=source.x + range; i++)
            for(int j=source.z - range; j <= source.z + range; j++)
                chunks.add(level.getChunk(i, j));
        return chunks.stream();
    }

    public static boolean containsBlockEntity(ChunkAccess chunk, Predicate<BlockEntity> condition) {
        return chunk.getBlockEntitiesPos().stream().anyMatch(pos -> condition.test(chunk.getBlockEntity(pos)));
    }
}
