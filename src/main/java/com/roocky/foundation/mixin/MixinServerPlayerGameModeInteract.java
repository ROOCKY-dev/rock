package com.roocky.foundation.mixin;

import com.roocky.foundation.api.event.ClaimEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerInteractionManager.class)
public class MixinServerPlayerGameModeInteract {

    @Shadow public ServerPlayerEntity player;

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void rock$onInteractBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        ActionResult result = ClaimEvents.BLOCK_INTERACT.invoker().check(player, world, hitResult.getBlockPos());
        if (result == ActionResult.FAIL) {
            cir.setReturnValue(ActionResult.FAIL);
            cir.cancel();
        }
    }
}
