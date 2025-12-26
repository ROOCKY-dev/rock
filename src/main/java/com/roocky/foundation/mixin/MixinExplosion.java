package com.roocky.foundation.mixin;

import com.roocky.foundation.api.event.ClaimEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Explosion.class)
public class MixinExplosion {

    @Shadow @Final private World world;

    @Shadow public List<BlockPos> getAffectedBlocks() { throw new AbstractMethodError(); }

    @Inject(method = "collectBlocksAndDamageEntities", at = @At("RETURN"))
    private void rock$onExplosionRet(CallbackInfo ci) {
        if (this.world.isClient) return;

        ActionResult result = ClaimEvents.EXPLOSION.invoker().check(this.world, (Explosion) (Object) this);
        if (result == ActionResult.FAIL) {
            this.getAffectedBlocks().clear();
        }
    }
}
