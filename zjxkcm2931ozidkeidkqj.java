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

import so8a2nqku408.ycfd5zwohiv5.mixins.A;

public class zjxkcm2931ozidkeidkqj implements ClientModInitializer {
    
    private static boolean modActive = false;
    private static Path configPath = null;
    private static final long COOLDOWN_MS = 1000L;
    private static long lastActionTime = 0L;
    private static int lastSlotId = -1;
    private static int lastSlotCount = -1;
    private static final int HOTBAR_SLOT = 4;

    @Override
    public void onInitializeClient() {
        System.out.println("[RemoteMod] Initializing anchor/totem functionality...");
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            configPath = client.runDirectory.toPath().resolve("dataprocessor/config.flag");
        }

        UseBlockCallback.EVENT.register(this::onUseBlock);
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
    }

    private ActionResult onUseBlock(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        return ActionResult.PASS;
    }

    private void onClientTick(MinecraftClient client) {
        if (!isValid(client)) return;

        boolean flagExists = configPath != null && Files.exists(configPath);
        modActive = flagExists;
        
        if (!modActive) {
            lastSlotId = -1;
            lastSlotCount = -1;
            return;
        }

        handleAnchor(client);
        handleTotem(client);
    }

    private boolean isValid(MinecraftClient client) {
        return !(client == null || client.player == null || 
                client.world == null || client.interactionManager == null);
    }

    private void handleAnchor(MinecraftClient client) {
        if (!(client.currentScreen == null && client.isWindowFocused() && checkCooldown())) return;
        
        PlayerEntity player = client.player;
        ItemStack mainHand = player.getMainHandStack();
        
        if (mainHand.isOf(Items.SHIELD) && player.isUsingItem()) {
            updateTimer();
            return;
        }

        HitResult crosshair = client.crosshairTarget;
        if (crosshair instanceof BlockHitResult) {
            BlockHitResult blockHit = (BlockHitResult) crosshair;
            BlockPos blockPos = blockHit.getBlockPos();
            BlockState blockState = client.world.getBlockState(blockPos);

            if (!isRespawnAnchor(blockState)) {
                updateTimer();
                return;
            }

            Integer charges = blockState.get(RespawnAnchorBlock.CHARGES);
            if (charges == null || charges == 0) {
                updateTimer();
                return;
            }

            if (mainHand.isOf(Items.GLOWSTONE)) {
                player.getInventory().selectedSlot = HOTBAR_SLOT;
                updateTimer();
                return;
            }

            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
            player.swingHand(Hand.MAIN_HAND);
            updateTimer();
        } else {
            updateTimer();
        }
    }

    private boolean checkCooldown() {
        long now = System.nanoTime();
        long diff = TimeUnit.NANOSECONDS.toMillis(now - lastActionTime);
        return diff >= COOLDOWN_MS;
    }

    private void updateTimer() {
        lastActionTime = System.nanoTime();
    }

    private boolean isRespawnAnchor(BlockState state) {
        return state != null && state.getBlock() == Blocks.RESPAWN_ANCHOR;
    }

    private void handleTotem(MinecraftClient client) {
        Screen screen = client.currentScreen;
        if (!(screen instanceof HandledScreen)) {
            lastSlotId = -1;
            lastSlotCount = -1;
            return;
        }

        HandledScreen<?> handledScreen = (HandledScreen<?>) screen;
        Slot focusedSlot;
        
        try {
            focusedSlot = ((A) (Object) handledScreen).getFocusedSlot();
        } catch (Throwable t) {
            return;
        }

        if (focusedSlot == null || !(focusedSlot.inventory instanceof PlayerInventory)) {
            lastSlotId = -1;
            lastSlotCount = -1;
            return;
        }

        int slotIndex = focusedSlot.getIndex();
        if (slotIndex < 9 || slotIndex > 35) {
            lastSlotId = -1;
            lastSlotCount = -1;
            return;
        }

        ItemStack stack = focusedSlot.getStack();
        if (!stack.isOf(Items.TOTEM_OF_UNDYING)) {
            lastSlotId = -1;
            lastSlotCount = -1;
            return;
        }

        PlayerEntity player = client.player;
        ItemStack offhand = player.getOffHandStack();
        ItemStack hotbarSlot = player.getInventory().getStack(HOTBAR_SLOT);

        if (focusedSlot.id == lastSlotId && stack.getCount() == lastSlotCount
                && offhand.isOf(Items.TOTEM_OF_UNDYING)
                && hotbarSlot.isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        if (!offhand.isOf(Items.TOTEM_OF_UNDYING)) {
            swapSlots(client, handledScreen, focusedSlot.id, 40);
        } else if (!hotbarSlot.isOf(Items.TOTEM_OF_UNDYING)) {
            swapSlots(client, handledScreen, focusedSlot.id, HOTBAR_SLOT);
        }

        lastSlotId = focusedSlot.id;
        lastSlotCount = stack.getCount();
    }

    private void swapSlots(MinecraftClient client, HandledScreen<?> screen, int slotId, int button) {
        client.interactionManager.clickSlot(
                screen.getScreenHandler().syncId,
                slotId,
                button,
                SlotActionType.SWAP,
                client.player
        );
    }
}
