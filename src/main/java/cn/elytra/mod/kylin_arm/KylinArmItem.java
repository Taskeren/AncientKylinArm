package cn.elytra.mod.kylin_arm;

import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import baubles.api.expanded.IBaubleExpanded;

public class KylinArmItem extends Item implements IBaubleExpanded {

    public KylinArmItem() {
        this.setUnlocalizedName("kylin_arm");
        this.setTextureName("kylin_arm:kylin_arm");
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setMaxStackSize(1);
    }

    private static boolean isItemStackKylinArm(ItemStack stack) {
        return stack != null && stack.getItem() instanceof KylinArmItem;
    }

    public static boolean isKylinArmEquipped(EntityPlayer player) {
        IInventory baubles = BaublesApi.getBaubles(player);
        for (int i = 0; i < baubles.getSizeInventory(); i++) { // whatever slots, just find the item
            ItemStack stack = baubles.getStackInSlot(i);
            if (isItemStackKylinArm(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.epic;
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> tooltips, boolean advanced) {
        super.addInformation(itemStack, player, tooltips, advanced);
        tooltips.add(I18n.format("item.kylin_arm.tooltips"));
    }

    @Override
    public String[] getBaubleTypes(ItemStack itemstack) {
        return KylinArmMod.anySlot ? new String[] { BaubleExpandedSlots.universalType }
            : new String[] { KylinArmMod.BAUBLE_TYPE };
    }

    @Override
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.UNIVERSAL;
    }

    @Override
    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        // no-op
    }

    @Override
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
        // no-op
    }

    @Override
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        // no-op
    }

    @Override
    public boolean canEquip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    @Override
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }
}
