package com.roocky.foundation;

import com.roocky.foundation.api.event.ClaimEvents;
import com.roocky.foundation.api.model.ClaimPermission;
import com.roocky.foundation.api.service.PermissionProvider;
import com.roocky.foundation.config.SimpleConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RockMod implements ModInitializer {
    public static final String MOD_ID = "rock";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("ROCK key components initializing...");
        
        SimpleConfig.load();
        
        // Networking Registration
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.playS2C().register(com.roocky.foundation.network.VisualPayload.ID, com.roocky.foundation.network.VisualPayload.CODEC);
        
        com.roocky.foundation.command.ClaimCommand.register();

        // Block Break
        ClaimEvents.BLOCK_BREAK.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(pos);
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                 PermissionProvider provider = RockAPI.getProvider();
                 if (!provider.checkPermission(serverPlayer, chunkPos, ClaimPermission.BLOCK_BREAK)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return ActionResult.FAIL;
                 }
            }
            return ActionResult.PASS;
        });

        // Block Place
        ClaimEvents.BLOCK_PLACE.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(pos);
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                 PermissionProvider provider = RockAPI.getProvider();
                 if (!provider.checkPermission(serverPlayer, chunkPos, ClaimPermission.BLOCK_PLACE)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return ActionResult.FAIL;
                 }
            }
            return ActionResult.PASS;
        });

        // Block Interact
        ClaimEvents.BLOCK_INTERACT.register((player, world, pos) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(pos);
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                BlockState state = world.getBlockState(pos);
                Block block = state.getBlock();

                ClaimPermission required = ClaimPermission.INTERACT_BLOCK;

                // Check if container
                boolean isContainer = state.hasBlockEntity() && 
                                     (world.getBlockEntity(pos) instanceof Inventory || 
                                      state.createScreenHandlerFactory(world, pos) != null);

                if (block instanceof EnderChestBlock) {
                    required = ClaimPermission.ENDER_CHEST;
                } else if (isContainer) {
                    required = ClaimPermission.CONTAINER_OPEN;
                } else if (block instanceof ButtonBlock) {
                    required = ClaimPermission.BUTTON_USE;
                } else if (block instanceof LeverBlock) {
                    required = ClaimPermission.LEVER_USE;
                } else if (block instanceof TrapdoorBlock) {
                    required = ClaimPermission.TRAPDOOR_OPEN;
                } else if (block instanceof FenceGateBlock) {
                    required = ClaimPermission.FENCE_GATE_OPEN;
                } else if (block instanceof NoteBlock) {
                    required = ClaimPermission.NOTE_BLOCK_USE;
                } else if (block instanceof JukeboxBlock) {
                    required = ClaimPermission.JUKEBOX_USE;
                } else if (block instanceof AbstractPressurePlateBlock) {
                    required = ClaimPermission.PRESSURE_PLATE;
                } else if (block instanceof ComparatorBlock || block instanceof RepeaterBlock) {
                    required = ClaimPermission.INTERACT_REDSTONE;
                }
                
                PermissionProvider provider = RockAPI.getProvider();
                 if (!provider.checkPermission(serverPlayer, chunkPos, required)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return ActionResult.FAIL;
                 }
            }
            return ActionResult.PASS;
        });
        
        // Entity Interact (Right Click)
        ClaimEvents.ENTITY_INTERACT.register((player, world, target) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(target.getBlockPos());
            
            if (player instanceof ServerPlayerEntity serverPlayer) {
                 PermissionProvider provider = RockAPI.getProvider();

                 ClaimPermission required = ClaimPermission.INTERACT_ENTITY;

                 if (target instanceof net.minecraft.entity.passive.AbstractHorseEntity ||
                     target instanceof net.minecraft.entity.passive.PigEntity ||
                     target instanceof net.minecraft.entity.passive.StriderEntity) {
                     required = ClaimPermission.RIDE_ANIMALS;
                 } else if (target instanceof net.minecraft.entity.vehicle.BoatEntity ||
                            target instanceof net.minecraft.entity.vehicle.MinecartEntity) {
                     required = ClaimPermission.VEHICLE_RIDE;
                 } else if (target instanceof net.minecraft.entity.passive.VillagerEntity ||
                            target instanceof net.minecraft.entity.passive.WanderingTraderEntity) {
                     required = ClaimPermission.VILLAGER_TRADE;
                 }

                 if (!provider.checkPermission(serverPlayer, chunkPos, required)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return ActionResult.FAIL;
                 }
            }
            return ActionResult.PASS;
        });

        // Attack Entity (Damage / PvP)
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient) return ActionResult.PASS;
            ChunkPos chunkPos = new ChunkPos(entity.getBlockPos());

            if (player instanceof ServerPlayerEntity serverPlayer) {
                PermissionProvider provider = RockAPI.getProvider();

                ClaimPermission required = ClaimPermission.ENTITY_DAMAGE;

                if (entity instanceof PlayerEntity) {
                    required = ClaimPermission.ENTITY_PVP;
                } else if (entity instanceof net.minecraft.entity.mob.Monster) {
                    required = ClaimPermission.HURT_MONSTERS;
                } else if (entity instanceof net.minecraft.entity.passive.AnimalEntity ||
                           entity instanceof net.minecraft.entity.passive.GolemEntity ||
                           entity instanceof net.minecraft.entity.passive.FishEntity ||
                           entity instanceof net.minecraft.entity.passive.SquidEntity) {
                    required = ClaimPermission.HURT_ANIMALS;
                } else if (entity instanceof net.minecraft.entity.vehicle.BoatEntity ||
                           entity instanceof net.minecraft.entity.vehicle.MinecartEntity) {
                    required = ClaimPermission.VEHICLE_BREAK;
                } else if (entity instanceof net.minecraft.entity.decoration.painting.PaintingEntity ||
                           entity instanceof net.minecraft.entity.decoration.ItemFrameEntity) {
                    required = ClaimPermission.PAINTING_BREAK;
                }

                if (!provider.checkPermission(serverPlayer, chunkPos, required)) {
                    sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        // Item Use (Right click item)
        UseItemCallback.EVENT.register((player, world, hand) -> {
             if (world.isClient) return TypedActionResult.pass(player.getStackInHand(hand));
             ChunkPos chunkPos = new ChunkPos(player.getBlockPos());

             if (player instanceof ServerPlayerEntity serverPlayer) {
                 PermissionProvider provider = RockAPI.getProvider();
                 if (!provider.checkPermission(serverPlayer, chunkPos, ClaimPermission.ITEM_USE)) {
                     sendFeedback(serverPlayer, provider.getDenialMessage(serverPlayer, chunkPos));
                     return TypedActionResult.fail(player.getStackInHand(hand));
                 }
             }
             return TypedActionResult.pass(player.getStackInHand(hand));
        });

        // Explosion
        ClaimEvents.EXPLOSION.register((world, explosion) -> {
            if (world.isClient) return ActionResult.PASS;
            if (SimpleConfig.get().enableExplosions) return ActionResult.PASS;

            com.roocky.foundation.impl.ClaimManager manager = com.roocky.foundation.impl.ClaimManager.get((net.minecraft.server.world.ServerWorld) world);
            
            for (net.minecraft.util.math.BlockPos pos : explosion.getAffectedBlocks()) {
                ChunkPos chunkPos = new ChunkPos(pos);
                com.roocky.foundation.api.model.Claim claim = manager.getClaim(chunkPos);

                if (claim != null && claim.getType() != com.roocky.foundation.api.model.ClaimType.WILDERNESS) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });
        
        LOGGER.info("ROCK fully initialized (API Mode).");
    }
    
    private void sendFeedback(ServerPlayerEntity player, String message) {
        player.sendMessage(Text.literal(message), true);
    }
}
