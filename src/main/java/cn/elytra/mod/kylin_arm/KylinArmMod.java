package cn.elytra.mod.kylin_arm;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import baubles.api.expanded.BaubleExpandedSlots;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@SuppressWarnings("UnstableApiUsage")
@NotNullByDefault
@Mod(modid = KylinArmMod.MOD_ID, version = Tags.VERSION)
public class KylinArmMod {

    public static final String MOD_ID = "kylin_arm";
    public static final String BAUBLE_TYPE = "kylin_arm";

    /// the item of kylin arm. available after pre-initialization.
    public static @Nullable KylinArmItem kylinArmItem;

    /// the indices of bauble slots of kylin arm type. available after post-initialization.
    @ApiStatus.Internal
    public static int @Nullable [] baubleTypeIds;

    /// The list of blocks that should not gain any bonus from Kylin Arms.
    public static List<Block> kylinArmBlacklist = Lists.newArrayList();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerItem(kylinArmItem = new KylinArmItem(), "kylin_arm");

        BaubleExpandedSlots.tryRegisterType(BAUBLE_TYPE);
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BAUBLE_TYPE, 1);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        baubleTypeIds = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(BAUBLE_TYPE);

        GameRegistry.addShapedRecipe(
            new ItemStack(kylinArmItem),
            "BPB",
            "ANH",
            "BSB",
            'B',
            Items.blaze_powder,
            'N',
            Items.nether_star,
            'P',
            Items.diamond_pickaxe,
            'A',
            Items.diamond_axe,
            'H',
            Items.diamond_hoe,
            'S',
            Items.diamond_shovel);
    }

    @SubscribeEvent
    public void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (KylinArmItem.isKylinArmEquipped(event.entityPlayer) && !kylinArmBlacklist.contains(event.block)) {
            event.success = true;
        }
    }

    @SubscribeEvent
    public void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (KylinArmItem.isKylinArmEquipped(event.entityPlayer)) {
            // FIXME: there's a bug where when the speed is way too high, the block is instant-break on the client, but
            // not the server, making de-sync.
            event.newSpeed = event.originalSpeed * 10.0F;
        }
    }
}
