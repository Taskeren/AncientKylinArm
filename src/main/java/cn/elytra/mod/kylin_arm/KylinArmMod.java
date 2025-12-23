package cn.elytra.mod.kylin_arm;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerEvent;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNullByDefault;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Lists;

import baubles.api.expanded.BaubleExpandedSlots;
import cpw.mods.fml.common.Loader;
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

    @SuppressWarnings("SpellCheckingInspection")
    public static final String ET_FUTURUM_MOD_ID = "etfuturum";

    /// true if Et Futurum Requiem is loaded.
    public static boolean hasEtFuturum;

    /// true if Kylin Arm is allowed to be equipped in any bauble slot.
    public static boolean anySlot = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        hasEtFuturum = Loader.isModLoaded(ET_FUTURUM_MOD_ID);

        GameRegistry.registerItem(kylinArmItem = new KylinArmItem(), "kylin_arm");

        BaubleExpandedSlots.tryRegisterType(BAUBLE_TYPE);
        BaubleExpandedSlots.tryAssignSlotsUpToMinimum(BAUBLE_TYPE, 1);

        MinecraftForge.EVENT_BUS.register(this);

        Configuration configuration = new Configuration(event.getSuggestedConfigurationFile());
        anySlot = configuration.getBoolean(
            "any-slot",
            Configuration.CATEGORY_GENERAL,
            false,
            "Whether or not allow Kylin Arm be equipped in any bauble slot.");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        baubleTypeIds = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(BAUBLE_TYPE);

        Object[] tools = { Items.diamond_pickaxe, Items.diamond_axe, Items.diamond_hoe, Items.diamond_shovel };
        if (hasEtFuturum) { // replace diamond tools to netherite ones
            tools[0] = findItemSafe(ET_FUTURUM_MOD_ID, "netherite_pickaxe");
            tools[1] = findItemSafe(ET_FUTURUM_MOD_ID, "netherite_axe");
            tools[2] = findItemSafe(ET_FUTURUM_MOD_ID, "netherite_hoe");
            tools[3] = findItemSafe(ET_FUTURUM_MOD_ID, "netherite_spade");
        }
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
            tools[0],
            'A',
            tools[1],
            'H',
            tools[2],
            'S',
            tools[3]);
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

    /// find the item from an external mod. if the target is null, returns an fire item with special name.
    private static Object findItemSafe(String modid, String name) {
        Item item = GameRegistry.findItem(modid, name);
        if (item != null) return item;
        ItemStack placeholder = new ItemStack(Blocks.fire);
        placeholder.setStackDisplayName("Placeholder of " + modid + ":" + name);
        return placeholder;
    }
}
