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

public class X implements ClientModInitializer {

    // master toggle via file
    private static boolean a = false;
    private static Path b = null;

    // anchor timing
    private static final long c = q(1L);
    private static long d = 0L;

    // hover totem cache
    private static int e = -1;
    private static int f = -1;

    // hotbar index (4) but obfuscated
    private static final int g = r((1 << 1), (1 << 1)) ^ 0x0;

    // string table
    private static final String[] h = new String[]{
            "ZGF0YXByb2Nlc3Nvci9jb25maWcuZmxhZw==", // "dataprocessor/config.flag"
            "cmVzcGF3bl9hbmNob3I=",
            "dG90ZW1fb2ZfdW5keWluZw==",
            "bm9pc2U=",   // noise
            "Ym9ndXM="    // noise
    };

    // crystal helper timing
    private static final long i0 = q(15L);
    private static long j0 = 0L;

    // random junk
    @SuppressWarnings("unused")
    private static final int[] k0 = new int[]{
            0x13, 0x37, 0x42, 0x24, 0x0, 0x1, 0x2
    };

    private static final Object L = new Object();

    @Override
    public void onInitializeClient() {
        o();
    }

    // ==========================
    // INIT
    // ==========================
    private static void o() {
        MinecraftClient mc = MinecraftClient.getInstance();
        Path tmp = null;

        if (mc != null) {
            byte[] raw = Base64.getDecoder().decode(h[0]);
            tmp = mc.runDirectory.toPath().resolve(new String(raw));
        }

        // extra branch noise
        if (tmp != null && tmp.toString().length() >= 0) {
            b = tmp;
        } else {
            b = tmp;
        }

        UseBlockCallback.EVENT.register(
                (u, w, hand, hit) -> ActionResult.PASS
        );

        ClientTickEvents.END_CLIENT_TICK.register(X::i);
    }

    // ==========================
    // TICK ENTRY
    // ==========================
    private static void i(MinecraftClient mc) {
        if (!s(mc)) return;

        // file flag
        boolean ok = (b != null && Files.exists(b));
        a = ok && !(!ok); // pointless double negation

        if (!a) {
            e = -1;
            f = -1;
            return;
        }

        // slightly scrambled order
        aa(mc); // crystal helper
        t(mc);  // anchor exploder
        u(mc);  // hover totem
    }

    // sanity check
    private static boolean s(MinecraftClient mc) {
        if (mc == null) return false;
        if (mc.player == null) return false;
        if (mc.world == null) return false;
        if (mc.interactionManager == null) return false;
        return true;
    }

    // ==========================
    // ANCHOR EXPLODER
    // ==========================
    private static void t(MinecraftClient mc) {
        // require no screen, focused, and cooldown
        if (!(mc.currentScreen == null && mc.isWindowFocused() && v())) {
            return;
        }

        ItemStack mh = mc.player.getMainHandStack();

        // shield using -> skip
        if (mh.isOf(Items.SHIELD) && mc.player.isUsingItem()) {
            return;
        }

        HitResult res = mc.crosshairTarget;
        if (res instanceof BlockHitResult) {
            BlockHitResult br = (BlockHitResult) res;
            BlockPos bp = br.getBlockPos();
            BlockState st = mc.world.getBlockState(bp);

            if (!w(st)) {
                k();
                return;
            }

            Integer ch = st.get(RespawnAnchorBlock.CHARGES);
            if (ch == null || ch == 0) {
                k();
                return;
            }

            // glowstone in main hand -> switch to hotbar g
            if (mh.isOf(Items.GLOWSTONE)) {
                mc.player.getInventory().selectedSlot = g;
                k();
                return;
            }

            // right-click anchor + swing
            mc.interactionManager.interactBlock(
                    mc.player,
                    Hand.MAIN_HAND,
                    br
            );
            mc.player.swingHand(Hand.MAIN_HAND);
            k();
        } else {
            // touch timer anyway
            k();
        }
    }

    private static boolean v() {
        long now = System.nanoTime();
        long diff = TimeUnit.NANOSECONDS.toMillis(now - d);
        // pointless xor
        long m = (diff ^ 0L);
        return m >= c;
    }

    private static void k() {
        d = System.nanoTime();
    }

    private static boolean w(BlockState st) {
        if (st == null) return false;
        // dead branch
        if (st.getBlock() == Blocks.AIR && st.getBlock() == Blocks.RESPAWN_ANCHOR) {
            return false;
        }
        return st.getBlock() == Blocks.RESPAWN_ANCHOR;
    }

    // ==========================
    // HOVER TOTEM
    // ==========================
    private static void u(MinecraftClient mc) {
        Screen sc = mc.currentScreen;

        if (!(sc instanceof HandledScreen)) {
            e = -1;
            f = -1;
            return;
        }

        HandledScreen<?> hs = (HandledScreen<?>) sc;

        Slot s0 = null;
        try {
            // mixin accessor, wrapped in noise cast
            s0 = ((so8a2nqku408.ycfd5zwohiv5.mixins.A) ((Object) hs)).getFocusedSlot();
        } catch (Throwable t0) {
            // swallow
            return;
        }

        if (s0 == null) {
            e = -1; f = -1;
            return;
        }

        if (!(s0.inventory instanceof PlayerInventory)) {
            e = -1; f = -1;
            return;
        }

        int idx = s0.getIndex();
        // only main inventory 9..35
        if (idx < 9 || idx > 35) {
            e = -1; f = -1;
            return;
        }

        ItemStack st = s0.getStack();
        if (!st.isOf(Items.TOTEM_OF_UNDYING)) {
            e = -1; f = -1;
            return;
        }

        ItemStack off = mc.player.getOffHandStack();
        ItemStack h5 = mc.player.getInventory().getStack(g);

        if (s0.id == e && st.getCount() == f
                && off.isOf(Items.TOTEM_OF_UNDYING)
                && h5.isOf(Items.TOTEM_OF_UNDYING)) {
            return;
        }

        // prefer offhand
        if (!off.isOf(Items.TOTEM_OF_UNDYING)) {
            x(mc, hs, s0.id, 40); // offhand slot
        } else if (!h5.isOf(Items.TOTEM_OF_UNDYING)) {
            x(mc, hs, s0.id, g);  // selected hotbar slot g
        }

        e = s0.id;
        f = st.getCount();
    }

    private static void x(
            MinecraftClient mc,
            HandledScreen<?> hs,
            int slotId,
            int button
    ) {
        // synchronized noise
        synchronized (L) {
            mc.interactionManager.clickSlot(
                    hs.getScreenHandler().syncId,
                    slotId,
                    button,
                    SlotActionType.SWAP,
                    mc.player
            );
        }
    }

    // dead helper noise (never used)
    @SuppressWarnings("unused")
    private static Slot y(HandledScreen<?> hs) {
        for (Slot s1 : hs.getScreenHandler().slots) {
            if (s1.inventory instanceof PlayerInventory) {
                int idx = s1.getIndex();
                if (idx >= 9 && idx <= 35 &&
                        s1.getStack().isOf(Items.TOTEM_OF_UNDYING)) {
                    return s1;
                }
            }
        }
        return null;
    }

    // ==========================
    // CRYSTAL HELPER (NO GUI)
    // ==========================
    private static void aa(MinecraftClient mc) {
        if (!s(mc)) return;

        // only when focused and no GUI
        if (mc.currentScreen != null || !mc.isWindowFocused()) return;

        // simple cooldown
        if (!ab()) return;

        HitResult hr = mc.crosshairTarget;
        if (hr == null) {
            ac();
            return;
        }

        // break entity if valid
        if (hr instanceof net.minecraft.util.hit.EntityHitResult) {
            net.minecraft.util.hit.EntityHitResult ehr = (net.minecraft.util.hit.EntityHitResult) hr;
            net.minecraft.entity.Entity ent = ehr.getEntity();

            if (ad(ent)) {
                net.minecraft.client.network.ClientPlayerInteractionManager im = mc.interactionManager;
                if (im != null) {
                    im.attackEntity(mc.player, ent);
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
                ac();
                return;
            }
        }

        // place crystal on bedrock/obsidian top
        if (hr instanceof BlockHitResult) {
            BlockHitResult bhr = (BlockHitResult) hr;
            BlockPos base = bhr.getBlockPos();
            if (base == null) {
                ac();
                return;
            }

            BlockState st = mc.world.getBlockState(base);
            if (!ae(st)) {
                ac();
                return;
            }

            BlockPos up = base.up();
            if (!af(mc, up)) {
                ac();
                return;
            }

            // requires holding right-click (using item)
            if (!mc.player.isUsingItem()) {
                // still tick timer a bit
                ac();
                return;
            }

            ItemStack mh = mc.player.getMainHandStack();
            int prev = mc.player.getInventory().selectedSlot;
            boolean swapped = false;

            if (!mh.isOf(Items.END_CRYSTAL)) {
                int slot = ag(mc);
                if (slot == -1) {
                    ac();
                    return;
                }
                if (slot != prev) {
                    mc.player.getInventory().selectedSlot = slot;
                    swapped = true;
                }
            }

            net.minecraft.client.network.ClientPlayerInteractionManager im = mc.interactionManager;
            if (im != null) {
                im.interactBlock(mc.player, Hand.MAIN_HAND, bhr);
                mc.player.swingHand(Hand.MAIN_HAND);
            }

            if (swapped) {
                mc.player.getInventory().selectedSlot = prev;
            }

            ac();
        } else {
            ac();
        }
    }

    private static boolean ab() {
        long now = System.nanoTime();
        long diff = TimeUnit.NANOSECONDS.toMillis(now - j0);
        // tiny obf
        return ((diff ^ 0x0L) >= i0);
    }

    private static void ac() {
        j0 = System.nanoTime();
    }

    private static boolean ad(net.minecraft.entity.Entity e0) {
        if (e0 == null) return false;
        // dead impossible condition
        if (e0 instanceof PlayerEntity && e0 instanceof net.minecraft.entity.decoration.EndCrystalEntity) {
            return false;
        }
        return (e0 instanceof net.minecraft.entity.decoration.EndCrystalEntity
                || e0 instanceof net.minecraft.entity.mob.SlimeEntity
                || e0 instanceof net.minecraft.entity.mob.MagmaCubeEntity);
    }

    private static boolean ae(BlockState st) {
        if (st == null) return false;
        BlockState mirror = st;
        return (mirror.isOf(Blocks.BEDROCK) || mirror.isOf(Blocks.OBSIDIAN));
    }

    private static boolean af(MinecraftClient mc, BlockPos bp) {
        if (mc == null || mc.world == null || bp == null) return false;

        if (!mc.world.getBlockState(bp).isAir()) {
            return false;
        }

        net.minecraft.util.math.Box box = new net.minecraft.util.math.Box(bp);
        java.util.List<net.minecraft.entity.Entity> list = mc.world.getOtherEntities(null, box);

        return list == null || list.isEmpty();
    }

    private static int ag(MinecraftClient mc) {
        if (mc == null || mc.player == null) return -1;

        PlayerInventory inv = mc.player.getInventory();

        // useless double-loop flattened to single
        for (int i = 0; i < 9; i++) {
            int idx = (i ^ 0x0); // pointless xor
            ItemStack st = inv.getStack(idx);
            if (st.isOf(Items.END_CRYSTAL)) {
                return idx;
            }
        }
        return -1;
    }

    // ==========================
    // TINY MATH OBF HELPERS
    // ==========================
    private static long q(long in) {
        // xor with 0, then +0 to look fancier
        return (in ^ 0L) + 0L;
    }

    private static int r(int a0, int a1) {
        int t = a0 * a1;
        return t ^ 0x0;
    }
}
