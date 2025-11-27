package so8a2nqku408.ycfd5zwohiv5;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import so8a2nqku408.ycfd5zwohiv5.mixins.A;

public class zjxkcm2931ozidkeidkqj implements ClientModInitializer {
    
    private static boolean _0x7F3A = false;
    private static Path _0x8B1C = null;
    private static final long _0x9D4E = 1000L;
    private static long _0xA5F2 = 0L;
    private static int _0xB8D7 = -1;
    private static int _0xC3A9 = -1;
    private static final int _0xD4B6 = 4;

    @Override
    public void onInitializeClient() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            _0x8B1C = client.runDirectory.toPath().resolve("dataprocessor/config.flag");
        }

        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> ActionResult.PASS);
        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
    }

    private void onTick(MinecraftClient client) {
        if (client.player == null || client.world == null || client.interactionManager == null) return;

        _0x7F3A = _0x8B1C != null && Files.exists(_0x8B1C);
        if (!_0x7F3A) {
            _0xB8D7 = -1;
            _0xC3A9 = -1;
            return;
        }

        handleAnchor(client);
        handleTotem(client);
    }

    private void handleAnchor(MinecraftClient client) {
        if (client.currentScreen != null || !client.isWindowFocused() || !checkCooldown()) return;
        
        PlayerEntity player = client.player;
        ItemStack mainHand = player.getMainHandStack();
        
        if (mainHand.isOf(Items.SHIELD) && player.isUsingItem()) {
            updateTimer();
            return;
        }

        HitResult crosshair = client.crosshairTarget;
        if (crosshair instanceof BlockHitResult hit) {
            BlockPos pos = hit.getBlockPos();
            BlockState state = client.world.getBlockState(pos);

            if (state.getBlock() != Blocks.RESPAWN_ANCHOR) {
                updateTimer();
                return;
            }

            Integer charges = state.get(RespawnAnchorBlock.CHARGES);
            if (charges == null || charges == 0) {
                updateTimer();
                return;
            }

            if (mainHand.isOf(Items.GLOWSTONE)) {
                player.getInventory().selectedSlot = _0xD4B6;
                updateTimer();
                return;
            }

            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, hit);
            player.swingHand(Hand.MAIN_HAND);
            updateTimer();
        } else {
            updateTimer();
        }
    }

    private boolean checkCooldown() {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - _0xA5F2) >= _0x9D4E;
    }

    private void updateTimer() {
        _0xA5F2 = System.nanoTime();
    }

    private void handleTotem(MinecraftClient client) {
        Screen screen = client.currentScreen;
        if (!(screen instanceof HandledScreen<?> hs)) {
            _0xB8D7 = -1;
            _0xC3A9 = -1;
            return;
        }

        Slot slot;
        try {
            slot = ((A) (Object) hs).getFocusedSlot();
        } catch (Throwable t) {
            return;
        }

        if (slot == null || !(slot.inventory instanceof PlayerInventory) || 
            slot.getIndex() < 9 || slot.getIndex() > 35 ||
            !slot.getStack().isOf(Items.TOTEM_OF_UNDYING)) {
            _0xB8D7 = -1;
            _0xC3A9 = -1;
            return;
        }

        PlayerEntity player = client.player;
        ItemStack offhand = player.getOffHandStack();
        ItemStack hotbar = player.getInventory().getStack(_0xD4B6);

        if (slot.id == _0xB8D7 && slot.getStack().getCount() == _0xC3A9
                && offhand.isOf(Items.TOTEM_OF_UNDYING)
                && hotbar.isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        if (!offhand.isOf(Items.TOTEM_OF_UNDYING)) {
            swap(client, hs, slot.id, 40);
        } else if (!hotbar.isOf(Items.TOTEM_OF_UNDYING)) {
            swap(client, hs, slot.id, _0xD4B6);
        }

        _0xB8D7 = slot.id;
        _0xC3A9 = slot.getStack().getCount();
    }

    private void swap(MinecraftClient client, HandledScreen<?> screen, int slotId, int button) {
        client.interactionManager.clickSlot(screen.getScreenHandler().syncId, slotId, button, SlotActionType.SWAP, client.player);
    }
}
