package grezde.pillagertrading.mixin;

import grezde.pillagertrading.client.PoseSyncUtil;
import grezde.pillagertrading.entity.PillagerTradeGoal;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraft.world.entity.monster.Pillager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(Pillager.class)
public abstract class PillagerMixin {

    @Inject(method = "getArmPose", at=@At("HEAD"), cancellable = true)
    public void getArmPose(CallbackInfoReturnable<AbstractIllager.IllagerArmPose> callbackInfoReturnable) {
        // very sussy code
        // client code here, so we must be gentle
        // needs rewrite
        //boolean match = ((Pillager)(Object)this).goalSelector.getAvailableGoals().stream().anyMatch(goal -> goal.getGoal() instanceof PillagerTradeGoal ptg && ptg.canUse());
        boolean match = PoseSyncUtil.isPassive((Pillager)(Object)this);
        if(match)
            callbackInfoReturnable.setReturnValue(AbstractIllager.IllagerArmPose.NEUTRAL);
    }
}
