package grezde.pillagertrading.util;

import grezde.pillagertrading.items.PTItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

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
}
