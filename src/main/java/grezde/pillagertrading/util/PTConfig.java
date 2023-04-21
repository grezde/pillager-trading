package grezde.pillagertrading.util;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class PTConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec CONFIG;

    public static ForgeConfigSpec.IntValue TRADING_COOLDOWN;
    public static ForgeConfigSpec.IntValue STATUE_RANGE;
    public static ForgeConfigSpec.IntValue STATUE_COOLDOWN;

    static {
        BUILDER.push("trading");
        TRADING_COOLDOWN = BUILDER
                .comment("The time period after which the pillager is able to trade again (in ticks)")
                .defineInRange("trading_cooldown", 6000, 1, 720000);
        BUILDER.pop();

        BUILDER.push("warding_statue");
        STATUE_RANGE = BUILDER
                .comment("The range in which the warding statue disables spawns")
                .defineInRange("statue_range", 6, 1, 16);
        STATUE_COOLDOWN = BUILDER
                .comment("The time period a Lapis Lazuli jewel will keep the statue activated (in ticks)")
                .defineInRange("statue_cooldown", 48000, 1, 720000);
        BUILDER.pop();

        CONFIG = BUILDER.build();
    }

    public static void load(ForgeConfigSpec spec, Path path) {
        CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();
        configData.load();
        spec.setConfig(configData);
    }

}
