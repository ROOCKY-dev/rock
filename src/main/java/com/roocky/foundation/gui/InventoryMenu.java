package com.roocky.foundation.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class InventoryMenu implements NamedScreenHandlerFactory {
    private final Text title;
    private final int rows;
    private final SimpleInventory inventory;
    private final Map<Integer, Consumer<ClickEvent>> actions = new HashMap<>();
    private Consumer<PlayerEntity> closeCallback;

    public InventoryMenu(Text title, int rows) {
        this.title = title;
        this.rows = rows;
        this.inventory = new SimpleInventory(rows * 9);
    }

    public void setItem(int slot, ItemStack stack, Consumer<ClickEvent> action) {
        inventory.setStack(slot, stack);
        if (action != null) {
            actions.put(slot, action);
        } else {
            actions.remove(slot);
        }
    }

    public void setItem(int slot, ItemStack stack) {
        setItem(slot, stack, null);
    }

    public void setCloseCallback(Consumer<PlayerEntity> callback) {
        this.closeCallback = callback;
    }

    public void open(ServerPlayerEntity player) {
        player.openHandledScreen(this);
    }

    @Override
    public Text getDisplayName() {
        return title;
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        ScreenHandlerType<?> type = switch (rows) {
            case 1 -> ScreenHandlerType.GENERIC_9X1;
            case 2 -> ScreenHandlerType.GENERIC_9X2;
            case 3 -> ScreenHandlerType.GENERIC_9X3;
            case 4 -> ScreenHandlerType.GENERIC_9X4;
            case 5 -> ScreenHandlerType.GENERIC_9X5;
            case 6 -> ScreenHandlerType.GENERIC_9X6;
            default -> throw new IllegalArgumentException("Invalid rows: " + rows);
        };

        return new GenericContainerScreenHandler(type, syncId, playerInventory, inventory, rows) {
            @Override
            public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
                if (slotIndex >= 0 && slotIndex < inventory.size()) {
                    // It's a click in our GUI
                    if (actions.containsKey(slotIndex)) {
                        actions.get(slotIndex).accept(new ClickEvent(player, slotIndex, button, actionType));
                    }
                    // Always cancel clicks in the GUI to prevent taking items
                    return;
                }

                // Block moving items into the GUI (Shift-Click from player inv)
                if (actionType == SlotActionType.QUICK_MOVE) {
                     return;
                }

                super.onSlotClick(slotIndex, button, actionType, player);
            }

            @Override
            public void onClosed(PlayerEntity player) {
                super.onClosed(player);
                if (closeCallback != null) closeCallback.accept(player);
            }
        };
    }

    public record ClickEvent(PlayerEntity player, int slot, int button, SlotActionType actionType) {}
}
