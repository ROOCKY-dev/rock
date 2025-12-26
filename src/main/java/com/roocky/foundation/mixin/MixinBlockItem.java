package com.roocky.foundation.mixin;

import com.roocky.foundation.api.event.ClaimEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class MixinBlockItem {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void rock$onPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            ActionResult result = ClaimEvents.BLOCK_PLACE.invoker().check(player, player.getWorld(), context.getBlockPos());
            if (result == ActionResult.FAIL) {
                cir.setReturnValue(ActionResult.FAIL);
                cir.cancel();
            }
        }
    }
}
