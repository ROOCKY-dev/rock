package com.roocky.foundation.mixin;

import com.roocky.foundation.api.event.ClaimEvents;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void rock$onAttack(Entity target, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ActionResult result = ClaimEvents.ENTITY_INTERACT.invoker().check(player, player.getWorld(), target);
        if (result == ActionResult.FAIL) {
            ci.cancel();
        }
    }
}
