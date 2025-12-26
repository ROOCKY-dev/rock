package com.roocky.foundation.mixin;

import com.roocky.foundation.api.event.ClaimEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerGameMode {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "tryBreakBlock", at = @At("HEAD"), cancellable = true)
    private void rock$onTryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        ActionResult result = ClaimEvents.BLOCK_BREAK.invoker().check(this.player, this.player.getWorld(), pos);
        if (result == ActionResult.FAIL) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
