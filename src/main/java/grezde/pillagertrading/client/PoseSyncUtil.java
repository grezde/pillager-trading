package grezde.pillagertrading.client;

import net.minecraft.world.entity.monster.Pillager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PoseSyncUtil {

    private static List<UUID> passivePillagers = new ArrayList<>();

    public static boolean isPassive(Pillager pillager) {
        return passivePillagers.contains(pillager.getUUID());
    }

    public static void addToList(UUID id) {
        passivePillagers.add(id);
    }

    public static void removeFromList(UUID id) {
        passivePillagers.remove(id);
    }

}
