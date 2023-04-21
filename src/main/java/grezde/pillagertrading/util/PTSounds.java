package grezde.pillagertrading.util;

import grezde.pillagertrading.PTMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class PTSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PTMod.MODID);

    public static final RegistryObject<SoundEvent> PILLAGER_TRADE = registerSound("pillager_trade");
    public static final RegistryObject<SoundEvent> PILLAGER_DECLINE = registerSound("pillager_decline");
    public static final RegistryObject<SoundEvent> STATUE_ACTIVATE = registerSound("statue_activate");
    public static final RegistryObject<SoundEvent> STATUE_DEACTIVATE = registerSound("statue_deactivate");

    private static RegistryObject<SoundEvent> registerSound(String name) {
        return SOUND_EVENTS.register(name, () -> new SoundEvent(new ResourceLocation(PTMod.MODID, name)));
    }

    public static void register(IEventBus modEbventBus) {
        SOUND_EVENTS.register(modEbventBus);
    }

}
