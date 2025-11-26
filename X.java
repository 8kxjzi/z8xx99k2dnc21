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
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import java.util.Random;

import so8a2nqku408.ycfd5zwohiv5.mixins.A;

public class X implements ClientModInitializer {
    private static boolean _0x7F3A = false;
    private static Path _0x8B1C = null;
    private static final long _0x9D4E = _0x2A6B(0x3E8L);
    private static long _0xA5F2 = 0L;
    private static int _0xB8D7 = -1;
    private static int _0xC3A9 = -1;
    private static final int _0xD4B6 = _0x1F8C(0x02, 0x02);
    
    private static final String[] _0xE7F1 = {
        _0x5C9A("ZGF0YXByb2Nlc3Nvci") + _0x5C9A("9jb25maWcuZmxhZw=="),
        _0x5C9A("cmVzcGF3bl9hbmNob3I="),
        _0x5C9A("dG90ZW1fb2ZfdW5keWluZw==")
    };
    
    private static final Random _0xF2D4 = new Random(0x7F3A8B1CL);

    @Override
    public void onInitializeClient() {
        _0x8F3D();
    }

    private static void _0x8F3D() {
        MinecraftClient _0x1A9B = MinecraftClient.getInstance();
        if (_0x1A9B != null) {
            String _0x2B7C = new String(Base64.getDecoder().decode(_0xE7F1[0]));
            _0x8B1C = _0x1A9B.runDirectory.toPath().resolve(_0x2B7C);
        }

        UseBlockCallback.EVENT.register(X::_0x4D2A);
        ClientTickEvents.END_CLIENT_TICK.register(X::_0x5E1B);
    }

    private static ActionResult _0x4D2A(PlayerEntity _0x6F9C, World _0x7A8D, Hand _0x8B9E, BlockHitResult _0x9CAF) {
        return ActionResult.PASS;
    }

    private static void _0x5E1B(MinecraftClient _0xBC2D) {
        if (!_0x7F9E(_0xBC2D)) return;
        boolean _0xCD3E = _0x8B1C != null && Files.exists(_0x8B1C);
        _0x7F3A = _0xCD3E;
        if (!_0x7F3A) {
            _0xB8D7 = -1;
            _0xC3A9 = -1;
            return;
        }
        _0x9D4F(_0xBC2D);
        _0xAE6A(_0xBC2D);
    }

    private static boolean _0x7F9E(MinecraftClient _0xDF7B) {
        return !(_0xDF7B == null || _0xDF7B.player == null || 
                _0xDF7B.world == null || _0xDF7B.interactionManager == null);
    }

    private static void _0x9D4F(MinecraftClient _0xE18C) {
        if (!(_0xE18C.currentScreen == null && _0xE18C.isWindowFocused() && _0xBF2C())) return;
        PlayerEntity _0xF29D = _0xE18C.player;
        ItemStack _0x13AE = _0xF29D.getMainHandStack();
        if (_0x13AE.isOf(Items.SHIELD) && _0xF29D.isUsingItem()) {
            _0xC4DF();
            return;
        }
        HitResult _0x24BF = _0xE18C.crosshairTarget;
        if (_0x24BF instanceof BlockHitResult) {
            BlockHitResult _0x35D0 = (BlockHitResult) _0x24BF;
            BlockPos _0x46E1 = _0x35D0.getBlockPos();
            BlockState _0x57F2 = _0xE18C.world.getBlockState(_0x46E1);
            if (!_0xD8A3(_0x57F2)) {
                _0xC4DF();
                return;
            }
            Integer _0x6903 = _0x57F2.get(RespawnAnchorBlock.CHARGES);
            if (_0x6903 == null || _0x6903 == 0) {
                _0xC4DF();
                return;
            }
            if (_0x13AE.isOf(Items.GLOWSTONE)) {
                _0xF29D.getInventory().selectedSlot = _0xD4B6;
                _0xC4DF();
                return;
            }
            _0xE18C.interactionManager.interactBlock(_0xE18C.player, Hand.MAIN_HAND, _0x35D0);
            _0xF29D.swingHand(Hand.MAIN_HAND);
            _0xC4DF();
        } else {
            _0xC4DF();
        }
    }

    private static boolean _0xBF2C() {
        long _0x7A14 = System.nanoTime();
        long _0x8B25 = TimeUnit.NANOSECONDS.toMillis(_0x7A14 - _0xA5F2);
        return _0x8B25 >= _0x9D4E;
    }

    private static void _0xC4DF() {
        _0xA5F2 = System.nanoTime();
    }

    private static boolean _0xD8A3(BlockState _0x9C36) {
        return _0x9C36 != null && _0x9C36.getBlock() == Blocks.RESPAWN_ANCHOR;
    }

    private static void _0xAE6A(MinecraftClient _0xAD47) {
        Screen _0xBE58 = _0xAD47.currentScreen;
        if (!(_0xBE58 instanceof HandledScreen)) {
            _0xB8D7 = -1;
            _0xC3A9 = -1;
            return;
        }
        HandledScreen<?> _0xCF69 = (HandledScreen<?>) _0xBE58;
        Slot _0xE07A;
        try {
            _0xE07A = ((A) (Object) _0xCF69).getFocusedSlot();
        } catch (Throwable _0xF18B) {
            return;
        }
        if (_0xE07A == null || !(_0xE07A.inventory instanceof PlayerInventory)) {
            _0xB8D7 = -1;
            _0xC3A9 = -1;
            return;
        }
        int _0x129C = _0xE07A.getIndex();
        if (_0x129C < 9 || _0x129C > 35) {
            _0xB8D7 = -1;
            _0xC3A9 = -1;
            return;
        }
        ItemStack _0x23AD = _0xE07A.getStack();
        if (!_0x23AD.isOf(Items.TOTEM_OF_UNDYING)) {
            _0xB8D7 = -1;
            _0xC3A9 = -1;
            return;
        }
        PlayerEntity _0x34BE = _0xAD47.player;
        ItemStack _0x45CF = _0x34BE.getOffHandStack();
        ItemStack _0x56D0 = _0x34BE.getInventory().getStack(_0xD4B6);
        if (_0xE07A.id == _0xB8D7 && _0x23AD.getCount() == _0xC3A9
                && _0x45CF.isOf(Items.TOTEM_OF_UNDYING)
                && _0x56D0.isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }
        if (!_0x45CF.isOf(Items.TOTEM_OF_UNDYING)) {
            _0xE9F1(_0xAD47, _0xCF69, _0xE07A.id, 40);
        } else if (!_0x56D0.isOf(Items.TOTEM_OF_UNDYING)) {
            _0xE9F1(_0xAD47, _0xCF69, _0xE07A.id, _0xD4B6);
        }
        _0xB8D7 = _0xE07A.id;
        _0xC3A9 = _0x23AD.getCount();
    }

    private static void _0xE9F1(MinecraftClient _0x67E1, HandledScreen<?> _0x78F2, int _0x8903, int _0x9A14) {
        _0x67E1.interactionManager.clickSlot(
                _0x78F2.getScreenHandler().syncId,
                _0x8903,
                _0x9A14,
                SlotActionType.SWAP,
                _0x67E1.player
        );
    }

    private static long _0x2A6B(long _0xDE58) {
        return _0xDE58 ^ 0x7F3A8B1CL;
    }

    private static int _0x1F8C(int _0xEF69, int _0xFE7A) {
        return (_0xEF69 << 1) | (_0xFE7A & 1);
    }

    private static String _0x5C9A(String _0x1A8B) {
        return new String(Base64.getDecoder().decode(_0x1A8B));
    }
}
